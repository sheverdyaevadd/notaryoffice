package com.notary.model;

import java.time.LocalDateTime;

public class ProvidedService {

    private int id;
    private int idDeal;
    private int idService;
    private Integer idDiscount;
    private LocalDateTime serviceDate;
    private double finalPrice;

    public ProvidedService(int id, int idDeal, int idService,
                           Integer idDiscount,
                           LocalDateTime serviceDate, double finalPrice) {
        this.id = id;
        this.idDeal = idDeal;
        this.idService = idService;
        this.idDiscount = idDiscount;
        this.serviceDate = serviceDate;
        this.finalPrice = finalPrice;
    }

    public int getId() { return id; }
    public int getIdDeal() { return idDeal; }
    public int getIdService() { return idService; }
    public Integer getIdDiscount() { return idDiscount; }
    public LocalDateTime getServiceDate() { return serviceDate; }
    public double getFinalPrice() { return finalPrice; }
}