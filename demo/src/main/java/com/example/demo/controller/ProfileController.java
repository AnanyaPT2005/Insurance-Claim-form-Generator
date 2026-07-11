package com.example.demo.controller;

import com.example.demo.entity.Patient;
import com.example.demo.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfileController {

    private final PatientService patientService;

    public ProfileController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {

        model.addAttribute("patient", new Patient());

        return "patient-profile";
    }

    @PostMapping("/profile")
    public String saveProfile(@ModelAttribute Patient patient) {

        patientService.savePatient(patient);

        return "redirect:/dashboard";
    }
}