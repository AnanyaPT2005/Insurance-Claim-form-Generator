package com.example.demo.controller;

import com.example.demo.entity.Claim;
import com.example.demo.entity.MedicalBill;
import com.example.demo.entity.Patient;
import com.example.demo.service.ClaimService;
import com.example.demo.service.MedicalBillService;
import com.example.demo.service.PatientService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/claims")
public class ClaimPageController {

    private final ClaimService claimService;
    private final MedicalBillService medicalBillService;
    private final PatientService patientService;

    public ClaimPageController(
            ClaimService claimService,
            MedicalBillService medicalBillService,
            PatientService patientService) {

        this.claimService = claimService;
        this.medicalBillService = medicalBillService;
        this.patientService = patientService;
    }

    @PostMapping("/save")
    public String saveClaim() {

        Patient patient = patientService
                .getPatientById(1L)
                .orElseThrow();

        // Get bills currently belonging to this patient
        List<MedicalBill> bills =
                medicalBillService.getAllBillsForPatient(patient);

        // Create claim
        Claim claim = new Claim();

        claim.setPatient(patient);

        claim.setClaimStatus("DRAFT");

        claim = claimService.saveClaim(claim);

        // Assign bills to this claim
        for (MedicalBill bill : bills) {

            if (bill.getClaim() == null) {

                bill.setClaim(claim);

                medicalBillService.saveBill(bill);
            }
        }

        return "redirect:/dashboard";
    }
}