package com.notary.model;

import java.math.BigDecimal;

public class Service {
    private int id;
    private String serviceName;
    private String description;
    private BigDecimal basePrice;

    public Service(int id, String serviceName,
                   String description, BigDecimal basePrice) {
        this.id = id;
        this.serviceName = serviceName;
        this.description = description;
        this.basePrice = basePrice;
    }

    public int getId() { return id; }
    public String getServiceName() { return serviceName; }
    public String getDescription() { return description; }
    public BigDecimal getBasePrice() { return basePrice; }
}