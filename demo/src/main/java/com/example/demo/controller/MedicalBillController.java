package com.example.demo.controller;

import com.example.demo.entity.MedicalBill;
import com.example.demo.service.MedicalBillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bills")
public class MedicalBillController {

    private final MedicalBillService medicalBillService;

    public MedicalBillController(MedicalBillService medicalBillService) {
        this.medicalBillService = medicalBillService;
    }

    @GetMapping
    public List<MedicalBill> getAllBills() {
        return medicalBillService.getAllBills();
    }

    @GetMapping("/{id}")
    public Optional<MedicalBill> getBill(@PathVariable Long id) {
        return medicalBillService.getBillById(id);
    }

    @PostMapping
    public MedicalBill saveBill(@RequestBody MedicalBill bill) {
        return medicalBillService.saveBill(bill);
    }

    @DeleteMapping("/{id}")
    public void deleteBill(@PathVariable Long id) {
        medicalBillService.deleteBill(id);
    }
}