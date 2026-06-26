package com.notary.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProvidedService {

    private int id;
    private int idDeal;
    private int idService;
    private List<Integer> discountIds;
    private LocalDateTime serviceDate;
    private double finalPrice;

    public ProvidedService(int id, int idDeal, int idService,
                           List<Integer> discountIds,
                           LocalDateTime serviceDate, double finalPrice) {
        this.id = id;
        this.idDeal = idDeal;
        this.idService = idService;
        this.discountIds = discountIds != null ? discountIds : new ArrayList<>();
        this.serviceDate = serviceDate;
        this.finalPrice = finalPrice;
    }

    public int getId() { return id; }
    public int getIdDeal() { return idDeal; }
    public int getIdService() { return idService; }
    public List<Integer> getDiscountIds() { return discountIds; }
    public LocalDateTime getServiceDate() { return serviceDate; }
    public double getFinalPrice() { return finalPrice; }
}
