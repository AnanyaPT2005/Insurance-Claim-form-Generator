package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    public String uploadBill(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes)
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

            billItemService.saveItem(item);
        }

        

        /*
         * Next step:
         *
         * Save response as Draft Bill
         */

        redirectAttributes.addFlashAttribute(
                "uploadedBill",
                response);

        return "redirect:/dashboard";
    }

    @PostMapping("/save")
    public String saveBill() {

        /*
         * Next step:
         *
         * Save MedicalBill
         * Save BillItems
         */

        return "redirect:/dashboard";
    }
}