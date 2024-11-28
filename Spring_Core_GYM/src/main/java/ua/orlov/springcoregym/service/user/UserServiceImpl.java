package ua.orlov.springcoregym.service.user;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.orlov.springcoregym.dao.impl.user.UserDao;
import ua.orlov.springcoregym.model.user.User;
import ua.orlov.springcoregym.service.password.PasswordService;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final Counter matchPasswordCounter;
    private final Counter changePasswordCounter;
    private final PasswordEncoder passwordEncoder;
    private final PasswordService passwordService;

    public UserServiceImpl(UserDao userDao, MeterRegistry meterRegistry, PasswordEncoder passwordEncoder,
                           PasswordService passwordService) {
        this.userDao = userDao;
        this.matchPasswordCounter = meterRegistry.counter("userService.isUserNameMatchPassword.count");
        this.changePasswordCounter = meterRegistry.counter("userService.changeUserPassword.count");
        this.passwordEncoder = passwordEncoder;
        this.passwordService = passwordService;
    }

    @Override
    @Timed(value = "userService.isUserNameMatchPassword", description = "Time taken to match username and password")
    public boolean isUserNameMatchPassword(String username, String password) {
        matchPasswordCounter.increment();

        User foundUser = getByUsername(username);

        return passwordEncoder.matches(password, foundUser.getPassword());
    }

    @Override
    @Timed(value = "userService.changeUserPassword", description = "Time taken to change the user password")
    public boolean changeUserPassword(String username, String oldPassword, String newPassword) {
        changePasswordCounter.increment();

        User foundUser = getByUsername(username);

        if (!passwordEncoder.matches(oldPassword, foundUser.getPassword())) {
            throw new BadCredentialsException("Wrong password = " + oldPassword);
        }

        if (newPassword == null || newPassword.length() != passwordService.getPasswordLength()) {
            newPassword = passwordService.generatePassword();
        }

        return userDao.changeUserPassword(username, passwordEncoder.encode(newPassword));
    }

    @Override
    public User getByUsername(String username) {
        return userDao.getByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username = " + username));
    }

    @Override
    public User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return getByUsername(username);
        } catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException("User not found with username = " + username);
        }
    }
}
