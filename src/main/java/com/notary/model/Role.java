package com.notary.model;

public class Role {
    private int id;
    private String roleType;

    public Role(int id, String roleType) {
        this.id = id;
        this.roleType = roleType;
    }

    public int getId() { return id; }
    public String getRoleType() { return roleType; }
}