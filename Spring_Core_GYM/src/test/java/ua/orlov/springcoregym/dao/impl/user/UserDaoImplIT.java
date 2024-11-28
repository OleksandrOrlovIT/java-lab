package ua.orlov.springcoregym.dao.impl.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ua.orlov.springcoregym.model.user.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/sql/user/populate_user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/prune_tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class UserDaoImplIT {

    @Autowired
    private UserDao userDao;

    private static final String USERNAME = "testUser";

    @Test
    void isUserNameMatchPasswordThenSuccess() {
        String password = "password";

        assertTrue(userDao.isUserNameMatchPassword(USERNAME, password));
    }

    @Test
    void isUserNameMatchPasswordThenFalse() {
        String password = "password";

        assertFalse(userDao.isUserNameMatchPassword(USERNAME + "ASDASD", password));
    }

    @Test
    void changeUserPasswordThenSuccess() {
        String newPassword = "newPassword";

        assertTrue(userDao.changeUserPassword(USERNAME, newPassword));
    }

    @Test
    void changeUserPasswordThenFailure() {
        String newPassword = "newPassword";

        assertFalse(userDao.changeUserPassword(USERNAME + "ASDSDA", newPassword));
    }

    @Test
    void getByUsernameThenSuccess() {
        Optional<User> user = userDao.getByUsername(USERNAME);

        assertTrue(user.isPresent());
        assertEquals(USERNAME, user.get().getUsername());
    }

    @Test
    void getByUsernameThenEmpty() {
        Optional<User> user = userDao.getByUsername(USERNAME + "ASD");

        assertTrue(user.isEmpty());
    }
}
