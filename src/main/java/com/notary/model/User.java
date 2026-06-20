package com.notary.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String login;
    private String passwordHash;
    private String email;
    private String phone;
    private LocalDateTime registrationDate;
    private int idRole;

    public User(int id, String login, String passwordHash,
                String email, String phone,
                LocalDateTime registrationDate, int idRole) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phone = phone;
        this.registrationDate = registrationDate;
        this.idRole = idRole;
    }

    public int getId() { return id; }
    public String getLogin() { return login; }
    public String getPasswordHash() { return passwordHash; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public int getIdRole() { return idRole; }
}