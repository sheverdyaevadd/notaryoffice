package com.notary.service;

import com.notary.dao.UserDAO;
import com.notary.model.User;

import java.sql.SQLException;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String login, String password) throws SQLException {
        User user = userDAO.findByLogin(login);

        if (user == null) return null;

        if (user.getPasswordHash().equals(password)) {
            return user;
        }

        return null;
    }
}