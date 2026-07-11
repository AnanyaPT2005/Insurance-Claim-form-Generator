package com.example.demo.repository;

import com.example.demo.entity.MedicalBill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalBillRepository extends JpaRepository<MedicalBill, Long> {
}