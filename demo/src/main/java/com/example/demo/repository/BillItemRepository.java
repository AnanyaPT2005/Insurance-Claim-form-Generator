package com.example.demo.repository;

import com.example.demo.entity.BillItem;
import com.example.demo.entity.MedicalBill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface BillItemRepository extends JpaRepository<BillItem, Long> {
    List<BillItem> findByMedicalBill(MedicalBill medicalBill);
}