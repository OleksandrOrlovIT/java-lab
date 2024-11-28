package ua.orlov.springcoregym.dao;

import java.util.Optional;

/**
 * Extension of the generic DAO interface to add a method for finding an entity by username.
 *
 * @param <E>  the type of the entity
 * @param <ID> the type of the entity identifier
 */
public interface DaoUsernameFindable<E, ID> extends Dao<E, ID> {

    /**
     * Retrieves an entity by its username.
     *
     * @param username the username of the entity
     * @return an {@link Optional} containing the found entity, or empty if not found
     */
    Optional<E> getByUsername(String username);
}
