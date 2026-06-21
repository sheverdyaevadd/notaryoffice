package com.notary.model;

import java.math.BigDecimal;

public class Discount {
    private int id;
    private String discountType;
    private BigDecimal discountSize;
    private String conditionsText;

    public Discount(int id, String discountType,
                    BigDecimal discountSize, String conditionsText) {
        this.id = id;
        this.discountType = discountType;
        this.discountSize = discountSize;
        this.conditionsText = conditionsText;
    }

    public int getId() { return id; }
    public String getDiscountType() { return discountType; }
    public BigDecimal getDiscountSize() { return discountSize; }
    public String getConditionsText() { return conditionsText; }
}