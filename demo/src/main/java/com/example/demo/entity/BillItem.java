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
        if (selected == null)
            selected = true;
    }

    public BillItem() {
    }

    public Long getItemId() {
        return itemId;
    }

    public MedicalBill getMedicalBill() {
        return medicalBill;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Column(nullable = false)
    private Boolean selected = true;

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public void setMedicalBill(MedicalBill medicalBill) {
        this.medicalBill = medicalBill;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}