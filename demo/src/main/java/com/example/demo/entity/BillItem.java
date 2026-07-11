package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bill_items")
public class BillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    private MedicalBill medicalBill;

    private String description;

    private Integer quantity;

    private BigDecimal price;

    @PrePersist
    public void prePersist() {
        if (quantity == null)
            quantity = 1;
    }
}