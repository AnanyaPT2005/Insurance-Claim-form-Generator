package com.example.demo.service;

import com.example.demo.entity.Claim;
import com.example.demo.entity.Patient;
import com.example.demo.repository.ClaimRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public Claim saveClaim(Claim claim) {
        return claimRepository.save(claim);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Optional<Claim> getClaimById(Long id) {
        return claimRepository.findById(id);
    }

    public void deleteClaim(Long id) {
        claimRepository.deleteById(id);
    }

    public List<Claim> getClaimsForPatient(Patient patient) {

        return claimRepository.findByPatient(patient);
    }
}