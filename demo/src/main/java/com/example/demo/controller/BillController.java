package com.example.demo.controller;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.OCRBillItem;
import com.example.demo.entity.BillItem;
import com.example.demo.service.BillItemService;

import com.example.demo.dto.OCRBillResponse;
import com.example.demo.entity.BillStatus;
import com.example.demo.entity.MedicalBill;
import com.example.demo.entity.Patient;
import com.example.demo.service.OCRService;
import com.example.demo.service.PatientService;
import com.example.demo.service.MedicalBillService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.math.BigDecimal;

import java.io.IOException;
import java.nio.file.*;

@Controller
@RequestMapping("/bills")
public class BillController {

        private static final String UPLOAD_DIR = "insurance-uploads";

        private final OCRService ocrService;

        private final MedicalBillService medicalBillService;
        private final PatientService patientService;
        private final BillItemService billItemService;

        public BillController(OCRService ocrService,
                        MedicalBillService medicalBillService,
                        PatientService patientService,
                        BillItemService billItemService) {

                this.ocrService = ocrService;
                this.medicalBillService = medicalBillService;
                this.patientService = patientService;
                this.billItemService = billItemService;
        }

        @PostMapping("/upload")
        public String uploadBill(@RequestParam("file") MultipartFile file)
                        throws IOException {

                if (file.isEmpty()) {
                        return "redirect:/dashboard";
                }

                Files.createDirectories(Paths.get(UPLOAD_DIR));

                String filename = System.currentTimeMillis() + "_"
                                + file.getOriginalFilename();

                Path path = Paths.get(UPLOAD_DIR, filename);

                Files.copy(file.getInputStream(),
                                path,
                                StandardCopyOption.REPLACE_EXISTING);

                OCRBillResponse response = ocrService.process(file);

                MedicalBill bill = new MedicalBill();

                bill.setHospitalName(response.getHospitalName());

                bill.setBillNumber(response.getBillNumber());

                bill.setBillDate(response.getBillDate());

                bill.setSubtotal(response.getSubtotal());

                bill.setTotalAmount(response.getTotal());

                bill.setOriginalFilePath(path.toString());

                bill.setStatus(BillStatus.DRAFT);

                /*
                 * Temporary until we fetch the logged-in patient's profile.
                 */
                Patient patient = patientService
                                .getPatientById(1L)
                                .orElseThrow();

                bill.setPatient(patient);

                bill = medicalBillService.saveBill(bill);
                System.out.println("Items found: " + response.getItems().size());

                for (OCRBillItem ocrItem : response.getItems()) {

                        BillItem item = new BillItem();

                        item.setMedicalBill(bill);

                        item.setDescription(
                                        ocrItem.getDescription());

                        item.setQuantity(
                                        ocrItem.getQuantity());

                        item.setPrice(
                                        ocrItem.getPrice());
                        item.setSelected(true);

                        billItemService.saveItem(item);
                }

                /*
                 * Next step:
                 *
                 * Save response as Draft Bill
                 */

                return "redirect:/dashboard";
        }

        @PostMapping("/save/{billId}")
        public String saveBill(

                        @PathVariable Long billId,

                        @RequestParam(required = false) List<Long> selected,

                        @RequestParam Map<String, String> formData

        ) {

                MedicalBill bill = medicalBillService
                                .getBillById(billId)
                                .orElseThrow(() -> new RuntimeException("Bill not found"));

                List<BillItem> items = billItemService.getItemsByBill(bill);

                Set<Long> selectedIds = new HashSet<>();

                if (selected != null) {
                        selectedIds.addAll(selected);
                }

                for (BillItem item : items) {

                        Long itemId = item.getItemId();

                        item.setSelected(
                                        selectedIds.contains(itemId));

                        item.setDescription(
                                        formData.get("description_" + itemId));

                        item.setQuantity(
                                        Integer.parseInt(
                                                        formData.get("quantity_" + itemId)));

                        item.setPrice(
                                        new BigDecimal(
                                                        formData.get("price_" + itemId)));
                        billItemService.saveItem(item);

                }

                bill.setStatus(BillStatus.SAVED);

                medicalBillService.saveBill(bill);

                return "redirect:/dashboard";
        }

        @PostMapping("/delete/{billId}")
        public String deleteBill(@PathVariable Long billId) {

                medicalBillService.deleteBill(billId);

                return "redirect:/dashboard";
        }

        @GetMapping("/file/{billId}")
        public ResponseEntity<Resource> viewBill(
                        @PathVariable Long billId) throws IOException {

                MedicalBill bill = medicalBillService
                                .getBillById(billId)
                                .orElseThrow(() -> new RuntimeException("Bill not found"));

                Path filePath = Paths.get(bill.getOriginalFilePath());

                if (!Files.exists(filePath)) {
                        return ResponseEntity.notFound().build();
                }

                Resource resource = new UrlResource(
                                filePath.toUri());

                String contentType = Files.probeContentType(filePath);

                if (contentType == null) {
                        contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                                .header(
                                                HttpHeaders.CONTENT_TYPE,
                                                contentType)
                                .header(
                                                HttpHeaders.CONTENT_DISPOSITION,
                                                "inline; filename=\"" +
                                                                filePath.getFileName() +
                                                                "\"")
                                .body(resource);
        }
}