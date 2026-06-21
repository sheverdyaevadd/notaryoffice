package com.notary.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Deal {
    private int id;
    private int idClient;
    private LocalDateTime dealDate;
    private BigDecimal totalAmount;
    private BigDecimal commission;

    public Deal(int id, int idClient, LocalDateTime dealDate,
                BigDecimal totalAmount, BigDecimal commission) {
        this.id = id;
        this.idClient = idClient;
        this.dealDate = dealDate;
        this.totalAmount = totalAmount;
        this.commission = commission;
    }

    public int getId() { return id; }
    public int getIdClient() { return idClient; }
    public LocalDateTime getDealDate() { return dealDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getCommission() { return commission; }
}