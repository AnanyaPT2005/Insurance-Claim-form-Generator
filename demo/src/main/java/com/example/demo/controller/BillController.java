
package com.example.demo.controller;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Locale;
import java.math.BigDecimal;
import java.io.IOException;
import java.io.InputStream;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;

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

    // Validate extension, size and actual file signature
    private void validateBillFile(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "File size exceeds the maximum allowed limit of 20 MB.");
        }

        String filename = file.getOriginalFilename();

        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Invalid filename.");
        }

        int dotIndex = filename.lastIndexOf('.');

        if (dotIndex < 0) {
            throw new IllegalArgumentException(
                    "Unsupported file format. Upload PDF, JPG, JPEG or PNG.");
        }

        String extension = filename.substring(dotIndex + 1)
                .toLowerCase(Locale.ROOT);

        byte[] header;

        try (InputStream input = file.getInputStream()) {
            header = input.readNBytes(8);
        }

        boolean validContent = switch (extension) {

            case "pdf" ->
                    startsWith(header, new byte[]{
                            0x25, 0x50, 0x44, 0x46, 0x2D
                    });

            case "jpg", "jpeg" ->
                    startsWith(header, new byte[]{
                            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF
                    });

            case "png" ->
                    startsWith(header, new byte[]{
                            (byte) 0x89, 0x50, 0x4E, 0x47,
                            0x0D, 0x0A, 0x1A, 0x0A
                    });

            default -> false;
        };

        if (!validContent) {
            throw new IllegalArgumentException(
                    "The file content does not match its extension. "
                    + "Upload a valid PDF, JPG, JPEG or PNG file.");
        }
    }

    private boolean startsWith(byte[] actual, byte[] expected) {

        if (actual.length < expected.length) {
            return false;
        }

        for (int i = 0; i < expected.length; i++) {
            if (actual[i] != expected[i]) {
                return false;
            }
        }

        return true;
    }

    @PostMapping("/upload")
    public String uploadBill(
            @RequestParam("file") MultipartFile file,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Validate before OCR or storage upload
        try {
            validateBillFile(file);
        } catch (IllegalArgumentException exception) {

            redirectAttributes.addFlashAttribute(
                    "uploadError", exception.getMessage());

            return "redirect:/dashboard";

        } catch (IOException exception) {

            redirectAttributes.addFlashAttribute(
                    "uploadError", "Unable to read the uploaded file.");

            return "redirect:/dashboard";
        }

        String objectPath = null;
        boolean billSaved = false;

        try {

            Patient patient = getCurrentPatient(authentication);

            // Existing OCR processing
            OCRBillResponse response = ocrService.process(file);

            // Upload original file to Supabase
            objectPath = storageService.upload(
                    file,
                    patient.getPatientId());

            MedicalBill bill = new MedicalBill();

            bill.setHospitalName(response.getHospitalName());
            bill.setBillNumber(response.getBillNumber());
            bill.setBillDate(response.getBillDate());
            bill.setSubtotal(response.getSubtotal());
            bill.setTotalAmount(response.getTotal());

            // Store Supabase object path
            bill.setOriginalFilePath(objectPath);

            bill.setStatus(BillStatus.DRAFT);
            bill.setPatient(patient);

            bill = medicalBillService.saveBill(bill);
            billSaved = true;

            System.out.println(
                    "Items found: " + response.getItems().size());

            // Existing OCR item creation
            for (OCRBillItem ocrItem : response.getItems()) {

                BillItem item = new BillItem();

                item.setMedicalBill(bill);
                item.setDescription(ocrItem.getDescription());
                item.setQuantity(ocrItem.getQuantity());
                item.setPrice(ocrItem.getPrice());
                item.setSelected(true);

                billItemService.saveItem(item);
            }

            redirectAttributes.addFlashAttribute(
                    "uploadSuccess", "Medical bill uploaded successfully.");

        } catch (Exception exception) {

            // Remove uploaded object if the database bill was not saved
            if (objectPath != null && !billSaved) {
                try {
                    storageService.delete(objectPath);
                } catch (RuntimeException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }

            if (billSaved) {
                redirectAttributes.addFlashAttribute(
                        "uploadError",
                        "The bill was uploaded, but some extracted items "
                        + "could not be saved. Please review the bill.");
            } else {
                redirectAttributes.addFlashAttribute(
                        "uploadError",
                        "Unable to upload or process the medical bill. "
                        + "Please try again.");
            }
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/save/{billId}")
    public String saveBill(
            @PathVariable Long billId,
            @RequestParam(required = false) List<Long> selected,
            @RequestParam Map<String, String> formData,
            Authentication authentication) {

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

        MedicalBill bill = getOwnedBill(billId, authentication);

        String objectPath = bill.getOriginalFilePath();

        if (objectPath != null && !objectPath.isBlank()) {
            storageService.delete(objectPath);
        }

        medicalBillService.deleteBill(billId);

        return "redirect:/dashboard";
    }

    @GetMapping("/file/{billId}")
    public ResponseEntity<Resource> viewBill(
            @PathVariable Long billId,
            Authentication authentication) {

        MedicalBill bill = getOwnedBill(billId, authentication);

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
