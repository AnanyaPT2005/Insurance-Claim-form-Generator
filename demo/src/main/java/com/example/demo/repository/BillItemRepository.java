package com.example.demo.repository;

import com.example.demo.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillItemRepository extends JpaRepository<BillItem, Long> {
}