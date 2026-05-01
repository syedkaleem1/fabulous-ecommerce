package com.fabulous.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationUtil {

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String phoneRegex = "^[+]?[0-9]{10,15}$";
        return phone.matches(phoneRegex);
    }

    public static boolean isPositive(Integer number) {
        return number != null && number > 0;
    }

    public static boolean isNonNegative(Integer number) {
        return number != null && number >= 0;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}