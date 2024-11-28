package ua.orlov.springcoregym.service.password;

import org.junit.jupiter.api.Test;
import ua.orlov.springcoregym.service.password.impl.PasswordServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceImplTest {
    @Test
    void generatePasswordThenSuccess() {
        PasswordServiceImpl passwordGenerator = new PasswordServiceImpl();
        String password = passwordGenerator.generatePassword();
        assertEquals(10, password.length());
    }

    @Test
    void getPasswordLengthThenSuccess() {
        PasswordServiceImpl passwordGenerator = new PasswordServiceImpl();
        assertEquals(10, passwordGenerator.getPasswordLength());
    }
}
