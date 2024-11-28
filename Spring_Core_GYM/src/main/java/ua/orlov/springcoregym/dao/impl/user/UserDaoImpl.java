package ua.orlov.springcoregym.dao.impl.user;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import ua.orlov.springcoregym.dao.AbstractDao;
import ua.orlov.springcoregym.model.user.User;

import java.util.Optional;

@Log4j2
@Repository
public class UserDaoImpl extends AbstractDao<User, Long> implements UserDao {

    private final String IS_USERNAME_PASSWORD_MATCH_QUERY;
    private final String CHANGE_PASSWORD_QUERY;
    private final String FIND_BY_USERNAME_QUERY;

    public UserDaoImpl() {
        IS_USERNAME_PASSWORD_MATCH_QUERY = "SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END " +
                "FROM " + getEntityClass().getSimpleName() + " u WHERE u.username = :username AND u.password = :password";
        CHANGE_PASSWORD_QUERY = "UPDATE " + getEntityClass().getSimpleName() + " u SET u.password = :newPassword " +
                "WHERE u.username = :username";
        FIND_BY_USERNAME_QUERY = "SELECT t FROM " + getEntityClass().getSimpleName() + " t WHERE t.username = :username";
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public boolean isUserNameMatchPassword(String username, String password) {
        TypedQuery<Boolean> query = getEntityManager()
                .createQuery(IS_USERNAME_PASSWORD_MATCH_QUERY, Boolean.class)
                .setParameter("username", username)
                .setParameter("password", password);

        return query.getSingleResult();
    }

    @Transactional
    @Override
    public boolean changeUserPassword(String username, String newPassword) {
        Query query = getEntityManager()
                .createQuery(CHANGE_PASSWORD_QUERY)
                .setParameter("username", username)
                .setParameter("newPassword", newPassword);

        return query.executeUpdate() == 1;
    }

    @Override
    public Optional<User> getByUsername(String username) {
        try {
            TypedQuery<User> query = getEntityManager().createQuery(FIND_BY_USERNAME_QUERY, User.class);
            query.setParameter("username", username);

            User user = query.getSingleResult();
            return Optional.of(user);

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
