package ua.orlov.springcoregym.service.user.trainee;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.orlov.springcoregym.dao.impl.user.UserDao;
import ua.orlov.springcoregym.dao.impl.user.trainee.TraineeDao;
import ua.orlov.springcoregym.dao.impl.user.trainer.TrainerDao;
import ua.orlov.springcoregym.dto.trainee.TraineeTrainingDTO;
import ua.orlov.springcoregym.exception.BusinessLogicException;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.model.user.Trainer;
import ua.orlov.springcoregym.service.password.PasswordService;

import java.util.*;

@Log4j2
@Service
@AllArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final TraineeDao traineeDAO;

    private final TrainerDao trainerDAO;

    private final PasswordService passwordService;

    private final UserDao userDao;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void deleteByUsername(String userName) {
        traineeDAO.deleteByUsername(userName);
    }

    @Transactional
    @Override
    public Trainee update(Trainee trainee) {
        checkNames(trainee);

        Trainee foundTrainee = getByUsername(trainee.getUsername());
        trainee.setId(foundTrainee.getId());
        trainee.setPassword(foundTrainee.getPassword());

        if (foundTrainee.isActive() != trainee.isActive()) {
            throw new BusinessLogicException("IsActive field can't be changed in update");
        }

        trainee.setTrainings(foundTrainee.getTrainings());


        trainee = traineeDAO.update(trainee);

        foundTrainee = getByUserNameWithTrainers(trainee.getUsername());

        return foundTrainee;
    }

    @Transactional
    @Override
    public Trainee create(Trainee trainee) {
        trainee.setUsername(constructTraineeUsername(trainee));

        checkAvailableUserName(trainee);

        if (trainee.getPassword() == null || trainee.getPassword().length() != passwordService.getPasswordLength()) {
            trainee.setPassword(passwordService.generatePassword());
        }

        String oldPassword = trainee.getPassword();
        trainee.setPassword(passwordEncoder.encode(trainee.getPassword()));

        Trainee createdTrainee = traineeDAO.create(trainee);

        return createdTrainee.toBuilder().password(oldPassword).build();
    }

    @Override
    public Trainee select(Long id) {
        return traineeDAO.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Trainee not found with id = " + id));
    }

    private void checkAvailableUserName(Trainee trainee) {
        userDao.getByUsername(trainee.getUsername())
                .ifPresent(foundTrainee -> {
                    trainee.setUsername(trainee.getUsername() + UUID.randomUUID());
                });
    }

    private String constructTraineeUsername(Trainee trainee) {
        checkFirstLastNames(trainee);

        return trainee.getFirstName() + "." + trainee.getLastName();
    }

    private void checkFirstLastNames(Trainee trainee) {
        Objects.requireNonNull(trainee, "Trainee can't be null");
        Objects.requireNonNull(trainee.getFirstName(), "Trainee's firstName can't be null");
        Objects.requireNonNull(trainee.getLastName(), "Trainee's lastName can't be null");
    }

    private void checkNames(Trainee trainee){
        checkFirstLastNames(trainee);
        Objects.requireNonNull(trainee.getUsername(), "Trainee.username can't be null");
    }

    @Override
    public boolean isUserNameMatchPassword(String username, String password) {
        Trainee foundTrainee = traineeDAO.getByUsername(username)
                .orElseThrow(() -> new BusinessLogicException("Trainee not found " + username));

        return password != null && password.equals(foundTrainee.getPassword());
    }

    @Transactional
    @Override
    public Trainee changePassword(Trainee trainee, String newPassword) {
        Trainee foundTrainee = select(trainee.getId());

        if (!foundTrainee.getPassword().equals(trainee.getPassword())) {
            throw new BusinessLogicException("Wrong password for trainee " + trainee.getUsername());
        }

        foundTrainee.setPassword(newPassword);

        return traineeDAO.update(foundTrainee);
    }

    @Transactional
    @Override
    public Trainee activateTrainee(Long traineeId) {
        Trainee foundTrainee = select(traineeId);

        if (foundTrainee.isActive()) {
            throw new BusinessLogicException("Trainee is already active " + foundTrainee);
        }

        foundTrainee.setActive(true);

        return traineeDAO.update(foundTrainee);
    }

    @Transactional
    @Override
    public Trainee deactivateTrainee(Long traineeId) {
        Trainee foundTrainee = select(traineeId);

        if (!foundTrainee.isActive()) {
            throw new BusinessLogicException("Trainee is already deactivated " + foundTrainee);
        }

        foundTrainee.setActive(false);

        return traineeDAO.update(foundTrainee);
    }

    @Override
    public List<Training> getTrainingsByTraineeTrainingDTO(TraineeTrainingDTO traineeTrainingDTO) {
        return traineeDAO.getTrainingsByTraineeTrainingDTO(traineeTrainingDTO);
    }

    @Override
    public List<Trainee> getAll() {
        return traineeDAO.getAll();
    }

    @Override
    public Trainee authenticateTrainee(String userName, String password) {
        Trainee foundTrainee = traineeDAO.getByUsername(userName)
                .orElseThrow(() -> new BusinessLogicException("Trainee not found " + userName));

        if (!foundTrainee.getPassword().equals(password)) {
            throw new BusinessLogicException("Wrong password for trainee " + userName);
        }

        return foundTrainee;
    }

    @Override
    public Trainee getByUsername(String traineeUsername) {
        return traineeDAO.getByUsername(traineeUsername)
                .orElseThrow(() -> new NoSuchElementException("Trainee not found " + traineeUsername));
    }

    @Transactional
    @Override
    public List<Trainer> updateTraineeTrainers(Long traineeId, List<Long> trainerIds) {
        Trainee trainee = select(traineeId);

        List<Trainer> trainers = trainerDAO.getByIds(trainerIds);
        if (trainers.isEmpty()) {
            throw new EntityNotFoundException("No trainers found with the provided IDs");
        }

        trainee.setTrainers(trainers);

        for (Trainer trainer : trainers) {
            if (trainer.getTrainees() == null) {
                trainer.setTrainees(new ArrayList<>());
            }

            if (!trainer.getTrainees().contains(trainee)) {
                trainer.getTrainees().add(trainee);
            }
        }

        trainee = traineeDAO.update(trainee);

        return traineeDAO.getTrainersByTraineeUsername(trainee.getUsername());
    }

    @Transactional
    @Override
    public List<Trainer> updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        Trainee trainee = getByUsername(traineeUsername);

        List<Trainer> trainers = trainerDAO.getByUsernames(trainerUsernames);
        if (trainers.isEmpty()) {
            throw new EntityNotFoundException("No trainers found with the provided userNames");
        }

        trainee.setTrainers(trainers);

        for (Trainer trainer : trainers) {
            if (trainer.getTrainees() == null) {
                trainer.setTrainees(new ArrayList<>());
            }

            if (!trainer.getTrainees().contains(trainee)) {
                trainer.getTrainees().add(trainee);
            }
        }

        trainee = traineeDAO.update(trainee);

        return traineeDAO.getTrainersByTraineeUsername(trainee.getUsername());
    }

    @Transactional
    @Override
    public Trainee getByUserNameWithTrainers(String traineeUsername) {
        Trainee trainee = getByUsername(traineeUsername);

        trainee.setTrainers(traineeDAO.getTrainersByTraineeUsername(traineeUsername));

        return trainee;
    }

    @Transactional
    @Override
    public void activateDeactivateTrainee(String traineeUsername, boolean isActive) {
        Trainee trainee = getByUsername(traineeUsername);

        if(!isActive){
            deactivateTrainee(trainee.getId());
        } else {
            activateTrainee(trainee.getId());
        }
    }
}
