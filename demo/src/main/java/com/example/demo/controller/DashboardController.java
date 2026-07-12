package com.example.demo.controller;

import com.example.demo.service.MedicalBillService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final MedicalBillService medicalBillService;

    public DashboardController(MedicalBillService medicalBillService) {
        this.medicalBillService = medicalBillService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("currentBills",
                medicalBillService.getAllBills());

        return "dashboard";
    }
}