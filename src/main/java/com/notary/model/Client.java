package com.notary.model;

public class Client {
    private int id;
    private String name;
    private String activityType;
    private String address;
    private String phone;

    public Client(int id, String name, String activityType,
                  String address, String phone) {
        this.id = id;
        this.name = name;
        this.activityType = activityType;
        this.address = address;
        this.phone = phone;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getActivityType() { return activityType; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
}