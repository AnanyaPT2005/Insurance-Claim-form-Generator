package com.example.demo.controller;

import com.example.demo.entity.Patient;
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

        public DashboardController(MedicalBillService medicalBillService,
                        PatientService patientService) {

                this.medicalBillService = medicalBillService;
                this.patientService = patientService;
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
                                new ArrayList<>());

                model.addAttribute(
                                "billRepository",
                                new ArrayList<>());

                return "dashboard";
        }

}