package com.example.demo.repository;

import com.example.demo.entity.MedicalBill;
import com.example.demo.entity.Patient;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.BillStatus;
import java.util.List;


public interface MedicalBillRepository extends JpaRepository<MedicalBill, Long> {
    List<MedicalBill> findByPatientAndStatus(
        Patient patient,
        BillStatus status);

    List<MedicalBill> findByPatient(Patient patient);
}