package ua.orlov.springcoregym.service.password;

/**
 * Service interface for password generation.
 */
public interface PasswordService {

    /**
     * Generates a random password with a predefined length and character set.
     *
     * @return a randomly generated password
     */
    String generatePassword();

    /**
     * Retrieves the predefined password length.
     *
     * @return the length of the generated password
     */
    Integer getPasswordLength();
}
