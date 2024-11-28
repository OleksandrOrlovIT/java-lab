package ua.orlov.springcoregym.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object (DAO) interface providing basic CRUD operations.
 *
 * @param <E>  the type of the entity
 * @param <ID> the type of the entity identifier
 */
public interface Dao<E, ID> {

    /**
     * Creates a new entity in the database.
     *
     * @param e the entity to create
     * @return the created entity
     */
    E create(E e);

    /**
     * Updates an existing entity in the database.
     *
     * @param e the entity to update
     * @return the updated entity
     */
    E update(E e);

    /**
     * Deletes an entity from the database by its identifier.
     *
     * @param id the identifier of the entity to delete
     */
    void deleteById(ID id);

    /**
     * Retrieves all entities from the database.
     *
     * @return a list of all entities
     */
    List<E> getAll();

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id the identifier of the entity
     * @return an {@link Optional} containing the found entity, or empty if not found
     */
    Optional<E> getById(ID id);
}
