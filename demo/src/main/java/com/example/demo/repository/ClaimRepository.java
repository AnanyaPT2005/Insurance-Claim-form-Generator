package com.example.demo.repository;

import com.example.demo.entity.Claim;
import com.example.demo.entity.Patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository
        extends JpaRepository<Claim, Long> {

    List<Claim> findByPatient(Patient patient);
}