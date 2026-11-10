package com.example.demo.controller;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.math.BigDecimal;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.OCRBillItem;
import com.example.demo.dto.OCRBillResponse;
import com.example.demo.entity.BillItem;
import com.example.demo.entity.BillStatus;
import com.example.demo.entity.MedicalBill;
import com.example.demo.entity.Patient;
import com.example.demo.entity.User;
import com.example.demo.service.BillItemService;
import com.example.demo.service.MedicalBillService;
import com.example.demo.service.OCRService;
import com.example.demo.service.PatientService;
import com.example.demo.service.UserService;
import com.example.demo.service.SupabaseStorageService;

@Controller
@RequestMapping("/bills")
public class BillController {

    private final OCRService ocrService;
    private final UserService userService;
    private final MedicalBillService medicalBillService;
    private final PatientService patientService;
    private final BillItemService billItemService;
    private final SupabaseStorageService storageService;

    public BillController(
            OCRService ocrService,
            MedicalBillService medicalBillService,
            PatientService patientService,
            BillItemService billItemService,
            UserService userService,
            SupabaseStorageService storageService) {

        this.ocrService = ocrService;
        this.medicalBillService = medicalBillService;
        this.patientService = patientService;
        this.billItemService = billItemService;
        this.userService = userService;
        this.storageService = storageService;
    }

    // Resolve the logged-in user's patient profile
    private Patient getCurrentPatient(Authentication authentication) {

        User user = userService.getUserByUsername(authentication.getName());

        return patientService.getPatientByUser(user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patient profile not found"));
    }

    // Retrieve a bill only if it belongs to the logged-in patient
    private MedicalBill getOwnedBill(
            Long billId,
            Authentication authentication) {

        Patient patient = getCurrentPatient(authentication);

        MedicalBill bill = medicalBillService.getBillById(billId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Bill not found"));

        if (bill.getPatient() == null ||
                !bill.getPatient().getPatientId().equals(patient.getPatientId())) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Bill not found");
        }

        return bill;
    }

    // Upload bill to Supabase Storage and save its object path in the database
    @PostMapping("/upload")
    public String uploadBill(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        if (file.isEmpty()) {
            return "redirect:/dashboard";
        }

        Patient patient = getCurrentPatient(authentication);

        // Process OCR using the uploaded file
        OCRBillResponse response = ocrService.process(file);

        // Upload the original bill to Supabase Storage
        String objectPath = storageService.upload(
                file,
                patient.getPatientId());

        MedicalBill bill = new MedicalBill();

        bill.setHospitalName(response.getHospitalName());
        bill.setBillNumber(response.getBillNumber());
        bill.setBillDate(response.getBillDate());
        bill.setSubtotal(response.getSubtotal());
        bill.setTotalAmount(response.getTotal());

        // Store the Supabase object path instead of a local file path
        bill.setOriginalFilePath(objectPath);

        bill.setStatus(BillStatus.DRAFT);
        bill.setPatient(patient);

        try {
            bill = medicalBillService.saveBill(bill);
        } catch (RuntimeException exception) {

            // Clean up uploaded file if saving the bill fails
            try {
                storageService.delete(objectPath);
            } catch (RuntimeException cleanupException) {
                exception.addSuppressed(cleanupException);
            }

            throw exception;
        }

        System.out.println("Items found: " + response.getItems().size());

        // Save OCR-extracted bill items
        for (OCRBillItem ocrItem : response.getItems()) {

            BillItem item = new BillItem();

            item.setMedicalBill(bill);
            item.setDescription(ocrItem.getDescription());
            item.setQuantity(ocrItem.getQuantity());
            item.setPrice(ocrItem.getPrice());
            item.setSelected(true);

            billItemService.saveItem(item);
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/save/{billId}")
    public String saveBill(
            @PathVariable Long billId,
            @RequestParam(required = false) List<Long> selected,
            @RequestParam Map<String, String> formData,
            Authentication authentication) {

        // Ownership check
        MedicalBill bill = getOwnedBill(billId, authentication);

        List<BillItem> items = billItemService.getItemsByBill(bill);

        Set<Long> selectedIds = new HashSet<>();

        if (selected != null) {
            selectedIds.addAll(selected);
        }

        for (BillItem item : items) {

            Long itemId = item.getItemId();

            item.setSelected(selectedIds.contains(itemId));

            item.setDescription(formData.get("description_" + itemId));

            item.setQuantity(
                    Integer.parseInt(formData.get("quantity_" + itemId)));

            item.setPrice(
                    new BigDecimal(formData.get("price_" + itemId)));

            billItemService.saveItem(item);
        }

        bill.setStatus(BillStatus.SAVED);

        medicalBillService.saveBill(bill);

        return "redirect:/dashboard";
    }

    @PostMapping("/delete/{billId}")
    public String deleteBill(
            @PathVariable Long billId,
            Authentication authentication) {

        // Ownership check
        MedicalBill bill = getOwnedBill(billId, authentication);

        // Delete the file from Supabase Storage first
        String objectPath = bill.getOriginalFilePath();

        if (objectPath != null && !objectPath.isBlank()) {
            storageService.delete(objectPath);
        }

        // Delete the database record and its associated bill items
        medicalBillService.deleteBill(billId);

        return "redirect:/dashboard";
    }

    @GetMapping("/file/{billId}")
    public ResponseEntity<Resource> viewBill(
            @PathVariable Long billId,
            Authentication authentication) {

        // Ownership check
        MedicalBill bill = getOwnedBill(billId, authentication);

        // Retrieve the file from private Supabase Storage
        SupabaseStorageService.StoredFile storedFile =
                storageService.download(bill.getOriginalFilePath());

        byte[] fileContent = storedFile.content();

        MediaType contentType = storedFile.contentType();

        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }

        Resource resource = new ByteArrayResource(fileContent);

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"medical-bill\"")
                .body(resource);
    }
}