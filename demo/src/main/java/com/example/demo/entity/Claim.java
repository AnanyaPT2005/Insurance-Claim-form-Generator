package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_id")
    private Long claimId;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "claim_number")
    private String claimNumber;

    @Column(name = "claim_status")
    private String claimStatus;

    @Column(name = "generated_pdf")
    private String generatedPdf;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "claim")
    private List<MedicalBill> medicalBills;

    public Claim() {
    }

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (claimStatus == null) {
            claimStatus = "DRAFT";
        }
    }

    // ---------- Getters ----------

    public Long getClaimId() {
        return claimId;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public String getGeneratedPdf() {
        return generatedPdf;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<MedicalBill> getMedicalBills() {
        return medicalBills;
    }

    // ---------- Setters ----------

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public void setGeneratedPdf(String generatedPdf) {
        this.generatedPdf = generatedPdf;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setMedicalBills(List<MedicalBill> medicalBills) {
        this.medicalBills = medicalBills;
    }
}