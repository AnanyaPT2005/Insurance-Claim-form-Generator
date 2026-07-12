package com.example.demo.controller.api;

import com.example.demo.entity.Claim;
import com.example.demo.service.ClaimService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping
    public List<Claim> getAllClaims() {
        return claimService.getAllClaims();
    }

    @GetMapping("/{id}")
    public Optional<Claim> getClaim(@PathVariable Long id) {
        return claimService.getClaimById(id);
    }

    @PostMapping
    public Claim saveClaim(@RequestBody Claim claim) {
        return claimService.saveClaim(claim);
    }

    @DeleteMapping("/{id}")
    public void deleteClaim(@PathVariable Long id) {
        claimService.deleteClaim(id);
    }
}