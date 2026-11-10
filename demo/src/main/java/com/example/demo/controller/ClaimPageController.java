package com.example.demo.controller;

import com.example.demo.entity.Claim;
import com.example.demo.entity.MedicalBill;
import com.example.demo.entity.Patient;
import com.example.demo.entity.User;

import com.example.demo.service.ClaimPdfService;
import com.example.demo.service.ClaimService;
import com.example.demo.service.MedicalBillService;
import com.example.demo.service.PatientService;
import com.example.demo.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/claims")
public class ClaimPageController {

    private final ClaimService claimService;
    private final MedicalBillService medicalBillService;
    private final PatientService patientService;
    private final ClaimPdfService claimPdfService;
    private final UserService userService;

    public ClaimPageController(
            ClaimService claimService,
            MedicalBillService medicalBillService,
            PatientService patientService,
            ClaimPdfService claimPdfService,
            UserService userService) {

        this.claimService = claimService;
        this.medicalBillService = medicalBillService;
        this.patientService = patientService;
        this.claimPdfService = claimPdfService;
        this.userService = userService;
    }

    // Get the logged-in user's patient profile
    private Patient getCurrentPatient(Authentication authentication) {

        User user = userService.getUserByUsername(authentication.getName());

        return patientService.getPatientByUser(user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patient profile not found"));
    }

    // Retrieve a claim only if it belongs to the logged-in patient
    private Claim getOwnedClaim(
            Long claimId,
            Authentication authentication) {

        Patient patient = getCurrentPatient(authentication);

        Claim claim = claimService.getClaimById(claimId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Claim not found"));

        if (claim.getPatient() == null ||
                !claim.getPatient().getPatientId().equals(patient.getPatientId())) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Claim not found");
        }

        return claim;
    }

    @PostMapping("/save")
    public String saveClaim(Authentication authentication) {

        Patient patient;

        try {
            patient = getCurrentPatient(authentication);
        } catch (ResponseStatusException ex) {
            return "redirect:/profile";
        }

        List<MedicalBill> bills =
                medicalBillService.getAllBillsForPatient(patient);

        Claim claim = new Claim();
        claim.setPatient(patient);
        claim.setClaimStatus("DRAFT");

        claim = claimService.saveClaim(claim);

        for (MedicalBill bill : bills) {
            if (bill.getClaim() == null) {
                bill.setClaim(claim);
                medicalBillService.saveBill(bill);
            }
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/generate/{claimId}")
    public ResponseEntity<Resource> generatePdf(
            @PathVariable Long claimId,
            Authentication authentication) throws IOException {

        // Ownership check
        Claim claim = getOwnedClaim(claimId, authentication);

        String pdfPath = claimPdfService.generateClaimPdf(claim);

        claim.setClaimNumber(
                "CLM-" + String.format("%06d", claim.getClaimId()));

        claim.setClaimStatus("GENERATED");
        claim.setGeneratedPdf(pdfPath);

        claimService.saveClaim(claim);

        Path path = Paths.get(pdfPath);

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("/pdf/{claimId}")
    public ResponseEntity<Resource> viewPdf(
            @PathVariable Long claimId,
            Authentication authentication) throws IOException {

        // Ownership check
        Claim claim = getOwnedClaim(claimId, authentication);

        if (claim.getGeneratedPdf() == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(claim.getGeneratedPdf());

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}