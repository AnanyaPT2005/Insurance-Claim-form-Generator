package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.OCRBillResponse;
import com.example.demo.service.OCRService;

import java.io.IOException;
import java.nio.file.*;

@Controller
@RequestMapping("/bills")
public class BillController {

    private static final String UPLOAD_DIR = "insurance-uploads";

    private final OCRService ocrService;

    public BillController(OCRService ocrService) {
        this.ocrService = ocrService;
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