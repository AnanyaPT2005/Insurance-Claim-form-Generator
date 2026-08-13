package com.example.demo.controller;

import com.example.demo.entity.Patient;
import com.example.demo.service.ClaimService;
import com.example.demo.service.MedicalBillService;
import com.example.demo.service.PatientService;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

        private final MedicalBillService medicalBillService;
        private final PatientService patientService;
        private final ClaimService claimService;

        public DashboardController(
                        MedicalBillService medicalBillService,
                        PatientService patientService,
                        ClaimService claimService) {

                this.medicalBillService = medicalBillService;
                this.patientService = patientService;
                this.claimService = claimService;
        }

        @GetMapping("/dashboard")
        public String dashboard(Model model) {

                // Temporary: fetch patient with ID 1
                Patient patient = patientService
                                .getPatientById(1L)
                                .orElseThrow();

                // model.addAttribute(
                // "currentBills",
                // medicalBillService.getDraftBills(patient));
                model.addAttribute(
                                "currentBills",
                                medicalBillService.getAllBillsForPatient(patient));

                model.addAttribute(
                                "claimHistory",
                                claimService.getClaimsForPatient(patient));

                model.addAttribute(
                                "billRepository",
                                medicalBillService.getAllBillsForPatient(patient));

                return "dashboard";
        }

}