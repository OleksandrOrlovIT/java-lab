package ua.orlov.springcoregym.dao.impl.training;

import org.springframework.stereotype.Repository;
import ua.orlov.springcoregym.dao.AbstractDao;
import ua.orlov.springcoregym.model.training.TrainingType;

@Repository
public class TrainingTypeDaoImpl extends AbstractDao<TrainingType, Long> implements TrainingTypeDao {
    @Override
    protected Class<TrainingType> getEntityClass() {
        return TrainingType.class;
    }
}
