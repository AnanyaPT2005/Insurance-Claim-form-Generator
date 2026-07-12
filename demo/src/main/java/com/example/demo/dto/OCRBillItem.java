package com.example.demo.dto;

import java.math.BigDecimal;

public class OCRBillItem {

    private String description;
    private Integer quantity;
    private BigDecimal price;

    public OCRBillItem() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}