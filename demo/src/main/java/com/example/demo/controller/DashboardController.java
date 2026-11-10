package com.example.demo.controller;

import com.example.demo.entity.Patient;
import com.example.demo.entity.User;
import com.example.demo.service.ClaimService;
import com.example.demo.service.MedicalBillService;
import com.example.demo.service.PatientService;
import com.example.demo.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final MedicalBillService medicalBillService;
    private final PatientService patientService;
    private final ClaimService claimService;
    private final UserService userService;

    public DashboardController(
            MedicalBillService medicalBillService,
            PatientService patientService,
            ClaimService claimService,
            UserService userService) {

        this.medicalBillService = medicalBillService;
        this.patientService = patientService;
        this.claimService = claimService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            Authentication authentication) {

        // Get the currently authenticated user
        User user = userService.getUserByUsername(
                authentication.getName());

        // Find the patient's profile associated with that user
        Patient patient = patientService
                .getPatientByUser(user)
                .orElse(null);

        // Redirect users who haven't completed their profile
        if (patient == null) {
            return "redirect:/profile";
        }

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