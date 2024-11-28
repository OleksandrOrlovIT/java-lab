package ua.orlov.springcoregym.service.password.impl;

import org.springframework.stereotype.Component;
import ua.orlov.springcoregym.service.password.PasswordService;

import java.security.SecureRandom;

@Component
public class PasswordServiceImpl implements PasswordService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int PASSWORD_LENGTH = 10;

    @Override
    public String generatePassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        while (sb.length() < PASSWORD_LENGTH) {
            char nextChar = CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length()));
            sb.append(nextChar);
        }
        return sb.toString();
    }

    @Override
    public Integer getPasswordLength() {
        return PASSWORD_LENGTH;
    }
}
