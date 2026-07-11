package com.example.demo.repository;

import com.example.demo.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
}