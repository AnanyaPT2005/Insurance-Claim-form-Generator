package com.example.demo.controller;

import com.example.demo.entity.Patient;
import com.example.demo.entity.User;
import com.example.demo.service.PatientService;
import com.example.demo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfileController {

    private final PatientService patientService;
    private final UserService userService;

    public ProfileController(PatientService patientService,
            UserService userService) {

        this.patientService = patientService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {

        String username = authentication.getName();

        User user = userService.getUserByUsername(username);

        Patient patient = patientService
                .getPatientByUser(user)
                .orElse(new Patient());

        patient.setUser(user);

        model.addAttribute("patient", patient);

        return "profile";
    }

    @PostMapping("/profile")
    public String saveProfile(@ModelAttribute Patient formPatient,
            Authentication authentication) {

        String username = authentication.getName();

        User user = userService.getUserByUsername(username);

        Patient patient = patientService
                .getPatientByUser(user)
                .orElse(new Patient());

        patient.setUser(user);

        // Copy editable fields
        patient.setFullName(formPatient.getFullName());
        patient.setDob(formPatient.getDob());
        patient.setGender(formPatient.getGender());
        patient.setPhone(formPatient.getPhone());
        patient.setEmail(formPatient.getEmail());
        patient.setAddress(formPatient.getAddress());
        patient.setPanNumber(formPatient.getPanNumber());
        patient.setInsuranceCompany(formPatient.getInsuranceCompany());
        patient.setPolicyNumber(formPatient.getPolicyNumber());
        patient.setMemberId(formPatient.getMemberId());

        patientService.savePatient(patient);

        return "redirect:/dashboard";
    }

}