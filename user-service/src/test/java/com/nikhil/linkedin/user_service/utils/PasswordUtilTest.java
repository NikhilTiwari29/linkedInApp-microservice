package com.nikhil.linkedin.user_service.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    @Test
    void hashPassword_andCheckPassword_match() {
        String hashed = PasswordUtil.hashPassword("secret123");
        assertTrue(PasswordUtil.checkPassword("secret123", hashed));
    }

    @Test
    void checkPassword_returnsFalseForWrongPassword() {
        String hashed = PasswordUtil.hashPassword("secret123");
        assertFalse(PasswordUtil.checkPassword("wrong", hashed));
    }
}
