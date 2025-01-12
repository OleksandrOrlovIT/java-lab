package ua.orlov.springcoregym.mapper.user;

import org.mapstruct.Mapper;
import ua.orlov.springcoregym.dto.user.UsernameUser;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    List<String> mapUsernameUserListToStringList(List<UsernameUser> usernameUsers);

    default String map(UsernameUser value) {
        return value != null ? value.getUsername() : null;
    }
}

