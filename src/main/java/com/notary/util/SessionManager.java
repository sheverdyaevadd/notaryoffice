package com.notary.util;

import com.notary.model.User;

public class SessionManager {

    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isSuperAdmin() {
        return currentUser != null && currentUser.getIdRole() == 3;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getIdRole() == 1;
    }

    public static boolean isOperator() {
        return currentUser != null && currentUser.getIdRole() == 2;
    }

    public static boolean isWatcher() {
        return currentUser != null && currentUser.getIdRole() == 4;
    }

    public static boolean canManageUsers() {
        return isSuperAdmin();
    }

    public static boolean canManageServiceTypes() {
        return isSuperAdmin() || isAdmin();
    }

    public static boolean canManageDiscountTypes() {
        return isSuperAdmin() || isAdmin();
    }

    public static boolean canEdit() {
        return isSuperAdmin() || isAdmin() || isOperator();
    }
}