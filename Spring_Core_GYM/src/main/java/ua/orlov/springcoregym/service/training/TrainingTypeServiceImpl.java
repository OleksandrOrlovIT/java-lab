package ua.orlov.springcoregym.service.training;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.orlov.springcoregym.dao.impl.training.TrainingTypeDao;
import ua.orlov.springcoregym.model.training.TrainingType;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeDao trainingTypeDao;

    @Override
    public TrainingType getById(Long id) {
        Objects.requireNonNull(id, "TrainingType id must not be null");

        return trainingTypeDao.getById(id)
                .orElseThrow(() -> new NoSuchElementException("TrainingType not found with id = " + id));
    }

    @Override
    public List<TrainingType> getAll() {
        return trainingTypeDao.getAll();
    }
}
