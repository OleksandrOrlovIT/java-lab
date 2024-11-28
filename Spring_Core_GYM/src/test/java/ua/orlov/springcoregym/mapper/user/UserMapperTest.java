package ua.orlov.springcoregym.mapper.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.orlov.springcoregym.dto.user.UsernameUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    @Test
    void mapUsernameUserListToStringList() {
        UsernameUser usernameUser1 = new UsernameUser("user1");
        UsernameUser usernameUser2 = new UsernameUser("user2");

        List<String> response = userMapper.mapUsernameUserListToStringList(List.of(usernameUser1, usernameUser2));

        assertNotNull(response);
        assertEquals(usernameUser1.getUsername(), response.get(0));
        assertEquals(usernameUser2.getUsername(), response.get(1));
    }
}
