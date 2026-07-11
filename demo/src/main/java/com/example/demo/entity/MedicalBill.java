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

    @Column(name = "original_file_path")
    private String originalFilePath;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @OneToMany(mappedBy = "medicalBill")
    private List<BillItem> billItems;

    @PrePersist
    public void prePersist() {
        uploadedAt = LocalDateTime.now();

        if (ocrStatus == null)
            ocrStatus = "SUCCESS";
    }
}