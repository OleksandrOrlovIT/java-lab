package ua.orlov.springcoregym.mapper.user;

import org.springframework.stereotype.Component;
import ua.orlov.springcoregym.dto.user.UsernameUser;

import java.util.List;

@Component
public class UserMapper {

    public List<String> mapUsernameUserListToStringList(List<UsernameUser> usernameUsers) {
        return usernameUsers.stream()
                .map(UsernameUser::getUsername)
                .toList();
    }
}
