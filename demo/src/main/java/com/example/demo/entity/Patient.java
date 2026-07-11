package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long patientId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private LocalDate dob;

    private String gender;

    private String phone;

    private String email;

    private String address;

    @Column(name = "pan_number")
    private String panNumber;

    @Column(name = "insurance_company")
    private String insuranceCompany;

    @Column(name = "policy_number")
    private String policyNumber;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "patient")
    private List<MedicalBill> medicalBills;

    @OneToMany(mappedBy = "patient")
    private List<Claim> claims;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}