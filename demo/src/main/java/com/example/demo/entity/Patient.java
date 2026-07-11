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

    @Column(name = "full_name")
    private String fullName;

    @Column
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

    public Patient() {
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    // ---------- Getters ----------

    public Long getPatientId() {
        return patientId;
    }

    public User getUser() {
        return user;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public String getMemberId() {
        return memberId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<MedicalBill> getMedicalBills() {
        return medicalBills;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    // ---------- Setters ----------

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setMedicalBills(List<MedicalBill> medicalBills) {
        this.medicalBills = medicalBills;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }
}