package com.example.demo.service;

import com.example.demo.entity.BillItem;
import com.example.demo.entity.BillStatus;
import com.example.demo.entity.MedicalBill;
import com.example.demo.entity.Patient;
import com.example.demo.repository.MedicalBillRepository;
import com.example.demo.entity.BillItem;
import com.example.demo.repository.BillItemRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalBillService {

    private final MedicalBillRepository medicalBillRepository;

    private final BillItemRepository billItemRepository;

    public MedicalBillService(
            MedicalBillRepository medicalBillRepository,
            BillItemRepository billItemRepository) {

        this.medicalBillRepository = medicalBillRepository;
        this.billItemRepository = billItemRepository;
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

    @Transactional
    public void deleteBill(Long id) {

        MedicalBill bill = medicalBillRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        List<BillItem> items = billItemRepository.findByMedicalBill(bill);

        billItemRepository.deleteAll(items);

        medicalBillRepository.delete(bill);
    }

    public List<MedicalBill> getAllBillsForPatient(Patient patient) {
        return medicalBillRepository.findByPatient(patient);
    }

}