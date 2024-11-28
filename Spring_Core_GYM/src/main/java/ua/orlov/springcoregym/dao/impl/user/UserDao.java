package ua.orlov.springcoregym.dao.impl.user;

import ua.orlov.springcoregym.dao.DaoUsernameFindable;
import ua.orlov.springcoregym.model.user.User;

public interface UserDao extends DaoUsernameFindable<User, Long> {

    boolean isUserNameMatchPassword(String username, String password);

    boolean changeUserPassword(String username, String newPassword);
}
