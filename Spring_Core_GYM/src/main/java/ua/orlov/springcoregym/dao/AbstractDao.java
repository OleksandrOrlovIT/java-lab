package ua.orlov.springcoregym.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T, ID> implements Dao<T, ID> {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public T create(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public T update(T entity) {
        return entityManager.merge(entity);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        Optional<T> entity = getById(id);
        entity.ifPresent(t -> entityManager.remove(t));
    }

    @Override
    public Optional<T> getById(ID id) {
        T entity = entityManager.find(getEntityClass(), id);

        return entity != null ? Optional.of(entity) : Optional.empty();
    }

    @Override
    public List<T> getAll() {
        String query = "SELECT e FROM " + getEntityClass().getSimpleName() + " e";
        return entityManager.createQuery(query, getEntityClass()).getResultList();
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected abstract Class<T> getEntityClass();
}
