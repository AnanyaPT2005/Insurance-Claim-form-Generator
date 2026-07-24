package com.example.demo.service;

import com.example.demo.entity.BillStatus;
import com.example.demo.entity.MedicalBill;
import com.example.demo.entity.Patient;
import com.example.demo.repository.MedicalBillRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalBillService {

    private final MedicalBillRepository medicalBillRepository;

    public MedicalBillService(MedicalBillRepository medicalBillRepository) {
        this.medicalBillRepository = medicalBillRepository;
    }

    public List<MedicalBill> getDraftBills(Patient patient) {

        return medicalBillRepository.findByPatientAndStatus(
                patient,
                BillStatus.DRAFT);

    }

    public MedicalBill saveBill(MedicalBill bill) {
        return medicalBillRepository.save(bill);
    }

    public List<MedicalBill> getAllBills() {
        return medicalBillRepository.findAll();
    }

    public Optional<MedicalBill> getBillById(Long id) {
        return medicalBillRepository.findById(id);
    }

    public void deleteBill(Long id) {
        medicalBillRepository.deleteById(id);
    }
}