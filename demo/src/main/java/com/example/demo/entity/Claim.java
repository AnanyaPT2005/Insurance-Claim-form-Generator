package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (claimStatus == null)
            claimStatus = "DRAFT";
    }
}