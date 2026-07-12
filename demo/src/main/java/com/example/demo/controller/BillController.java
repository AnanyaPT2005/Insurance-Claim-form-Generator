package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Controller
@RequestMapping("/bills")
public class BillController {

    private static final String UPLOAD_DIR = "insurance-uploads";

    @PostMapping("/upload")
    public String uploadBill(@RequestParam("file") MultipartFile file,
            Model model) throws IOException {

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

        model.addAttribute("uploadedFile", path.toString());

        /*
         * Next step:
         *
         * OCRService.process(path)
         *
         * model.addAttribute("ocr", response);
         */

        return "review-bill";
    }

}