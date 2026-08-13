package com.example.demo.service;

import com.example.demo.entity.BillItem;
import com.example.demo.entity.Claim;
import com.example.demo.entity.MedicalBill;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ClaimPdfService {

    private static final String PDF_DIR = "insurance-claims";

    public String generateClaimPdf(Claim claim) throws IOException {

        Files.createDirectories(Paths.get(PDF_DIR));

        String claimNumber =
                "CLM-" + String.format("%06d", claim.getClaimId());

        Path pdfPath = Paths.get(
                PDF_DIR,
                claimNumber + ".pdf"
        );

        try (PDDocument document = new PDDocument()) {

            PDPage page =
                    new PDPage(PDRectangle.A4);

            document.addPage(page);

            try (PDPageContentStream content =
                         new PDPageContentStream(
                                 document,
                                 page)) {

                float y = 780;

                // -------------------------
                // Title
                // -------------------------

                content.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA_BOLD),
                        20);

                content.beginText();
                content.newLineAtOffset(180, y);
                content.showText("HEALTH INSURANCE CLAIM");
                content.endText();

                y -= 40;

                // -------------------------
                // Claim information
                // -------------------------

                content.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA),
                        11);

                writeLine(
                        content,
                        "Claim Number: " + claimNumber,
                        60,
                        y);

                y -= 20;

                writeLine(
                        content,
                        "Status: GENERATED",
                        60,
                        y);

                y -= 20;

                writeLine(
                        content,
                        "Created: " + claim.getCreatedAt(),
                        60,
                        y);

                y -= 40;

                // -------------------------
                // Patient
                // -------------------------

                content.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA_BOLD),
                        13);

                writeLine(
                        content,
                        "Patient Information",
                        60,
                        y);

                y -= 25;

                content.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA),
                        11);

                if (claim.getPatient() != null) {

                    writeLine(
                            content,
                            "Patient ID: "
                                    + claim.getPatient().getPatientId(),
                            60,
                            y);

                    y -= 20;
                }

                y -= 20;

                // -------------------------
                // Bills
                // -------------------------

                content.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA_BOLD),
                        13);

                writeLine(
                        content,
                        "Medical Bills",
                        60,
                        y);

                y -= 25;

                content.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA),
                        10);

                BigDecimal claimTotal =
                        BigDecimal.ZERO;

                if (claim.getMedicalBills() != null) {

                    for (MedicalBill bill :
                            claim.getMedicalBills()) {

                        writeLine(
                                content,
                                "Bill: "
                                        + safe(bill.getBillNumber()),
                                60,
                                y);

                        y -= 18;

                        writeLine(
                                content,
                                "Hospital/Pharmacy: "
                                        + safe(bill.getHospitalName()),
                                80,
                                y);

                        y -= 18;

                        BigDecimal selectedAmount =
                                calculateSelectedAmount(bill);

                        writeLine(
                                content,
                                "Selected Amount: INR "
                                        + selectedAmount,
                                80,
                                y);

                        claimTotal =
                                claimTotal.add(selectedAmount);

                        y -= 25;

                        if (y < 100) {

                            content.close();

                            page =
                                    new PDPage(PDRectangle.A4);

                            document.addPage(page);

                            // For our first version,
                            // keep remaining content simple.
                            break;
                        }
                    }
                }

                // -------------------------
                // Claim total
                // -------------------------

                y -= 15;

                content.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA_BOLD),
                        14);

                writeLine(
                        content,
                        "TOTAL CLAIMED: INR "
                                + claimTotal,
                        60,
                        y);

                y -= 40;

                content.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA),
                        9);

                writeLine(
                        content,
                        "Generated by Insurance Claim System",
                        60,
                        y);
            }

            document.save(pdfPath.toFile());
        }

        return pdfPath.toString();
    }

    private BigDecimal calculateSelectedAmount(
            MedicalBill bill) {

        BigDecimal total =
                BigDecimal.ZERO;

        if (bill.getBillItems() == null) {
            return total;
        }

        for (BillItem item :
                bill.getBillItems()) {

            if (Boolean.TRUE.equals(
                    item.getSelected())) {

                BigDecimal price =
                        item.getPrice() == null
                                ? BigDecimal.ZERO
                                : item.getPrice();

                int quantity =
                        item.getQuantity() == null
                                ? 1
                                : item.getQuantity();

                total = total.add(
                        price.multiply(
                                BigDecimal.valueOf(quantity)));
            }
        }

        return total;
    }

    private String safe(String value) {

        return value == null
                ? "N/A"
                : value;
    }

    private void writeLine(
            PDPageContentStream content,
            String text,
            float x,
            float y)
            throws IOException {

        content.beginText();

        content.newLineAtOffset(x, y);

        content.showText(text);

        content.endText();
    }
}