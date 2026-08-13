package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "medical_bills")
public class MedicalBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")
    private Long billId;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "hospital_name")
    private String hospitalName;

    @Column(name = "bill_number")
    private String billNumber;

    @Column(name = "bill_date")
    private LocalDate billDate;

    private BigDecimal subtotal;

    private BigDecimal tax;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "ocr_status")
    private String ocrStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BillStatus status = BillStatus.DRAFT;

    @Column(name = "original_file_path")
    private String originalFilePath;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @OneToMany(mappedBy = "medicalBill", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BillItem> billItems;

    public MedicalBill() {
    }

    @PrePersist
    public void prePersist() {

        uploadedAt = LocalDateTime.now();

        if (ocrStatus == null)
            ocrStatus = "SUCCESS";

        if (status == null)
            status = BillStatus.DRAFT;
    }

    public BigDecimal getSelectedAmount() {

        if (billItems == null) {
            return BigDecimal.ZERO;
        }

        return billItems.stream()
                .filter(item -> Boolean.TRUE.equals(item.getSelected()))
                .map(item -> item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ---------- Getters ----------

    public Long getBillId() {
        return billId;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getOcrStatus() {
        return ocrStatus;
    }

    public BillStatus getStatus() {
        return status;
    }

    public String getOriginalFilePath() {
        return originalFilePath;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public List<BillItem> getBillItems() {
        return billItems;
    }

    // ---------- Setters ----------

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setOcrStatus(String ocrStatus) {
        this.ocrStatus = ocrStatus;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public void setOriginalFilePath(String originalFilePath) {
        this.originalFilePath = originalFilePath;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }

}