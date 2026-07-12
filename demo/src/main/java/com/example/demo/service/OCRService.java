package com.example.demo.service;

import com.example.demo.dto.OCRBillItem;
import com.example.demo.dto.OCRBillResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OCRService {

    public OCRBillResponse process(MultipartFile file) {

        /*
         * Temporary stub.
         *
         * Next:
         * 1. Save file
         * 2. Send to Veryfi
         * 3. Parse response
         */

        OCRBillResponse response = new OCRBillResponse();

        response.setHospitalName("Demo Hospital");
        response.setBillNumber("BILL-001");
        response.setUploadedFilePath(file.getOriginalFilename());
        response.setBillDate(LocalDate.now());
        response.setTotal(new BigDecimal("5420"));

        OCRBillItem item = new OCRBillItem();

        item.setDescription("Paracetamol");
        item.setQuantity(2);
        item.setPrice(new java.math.BigDecimal("25"));

        response.getItems().add(item);

        return response;
    }

}