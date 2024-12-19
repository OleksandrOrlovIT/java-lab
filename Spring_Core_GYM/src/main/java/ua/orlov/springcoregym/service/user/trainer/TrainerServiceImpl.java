package ua.orlov.springcoregym.service.user.trainer;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.orlov.springcoregym.dao.impl.user.UserDao;
import ua.orlov.springcoregym.dao.impl.user.trainee.TraineeDao;
import ua.orlov.springcoregym.dao.impl.user.trainer.TrainerDao;
import ua.orlov.springcoregym.dto.trainer.TrainerRegister;
import ua.orlov.springcoregym.dto.trainer.UpdateTrainerRequest;
import ua.orlov.springcoregym.exception.BusinessLogicException;
import ua.orlov.springcoregym.mapper.trainer.TrainerMapper;
import ua.orlov.springcoregym.model.training.Training;
import ua.orlov.springcoregym.model.training.TrainingType;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.model.user.Trainer;
import ua.orlov.springcoregym.service.password.PasswordService;
import ua.orlov.springcoregym.model.page.Pageable;
import ua.orlov.springcoregym.service.training.TrainingTypeService;
import ua.orlov.springcoregym.service.training.TrainingTypeServiceImpl;

import java.time.LocalDate;
import java.util.*;

@Log4j2
@AllArgsConstructor
@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDao trainerDAO;

    private final PasswordService passwordService;

    private final TraineeDao traineeDAO;

    private final UserDao userDao;

    private final PasswordEncoder passwordEncoder;

    private final TrainingTypeService trainingTypeService;

    private final TrainerMapper trainerMapper;

    @Transactional
    @Override
    public Trainer update(Trainer trainer) {
        checkNames(trainer);

        Trainer foundTrainer = getByUsername(trainer.getUsername());
        trainer.setId(foundTrainer.getId());
        trainer.setPassword(foundTrainer.getPassword());

        if (foundTrainer.isActive() != trainer.isActive()) {
            throw new BusinessLogicException("active field can't be changed in update");
        }

        trainer = trainerDAO.update(trainer);

        foundTrainer = getByUserNameWithTrainees(trainer.getUsername());

        return foundTrainer;
    }

    @Transactional
    @Override
    public Trainer updateFromUpdateTrainerRequest(UpdateTrainerRequest updateTrainerRequest) {
        TrainingType trainingType = trainingTypeService.getById(updateTrainerRequest.getSpecializationId());

        return update(trainerMapper.updateTrainerRequestToTrainer(updateTrainerRequest, trainingType));
    }

    @Override
    @Transactional
    public Trainer createFromTrainerRegister(TrainerRegister trainerRegister) {
        Trainer trainer = trainerMapper.trainerRegisterToTrainer(trainerRegister);

        TrainingType foundType = trainingTypeService.getById(trainerRegister.getSpecializationId());
        trainer.setSpecialization(foundType);

        return create(trainer);
    }

    @Transactional
    @Override
    public Trainer create(Trainer trainer) {
        trainer.setUsername(constructTrainerUsername(trainer));

        checkAvailableUserName(trainer);

        if (trainer.getPassword() == null || trainer.getPassword().length() != passwordService.getPasswordLength()) {
            trainer.setPassword(passwordService.generatePassword());
        }

        String oldPassword = trainer.getPassword();
        trainer.setPassword(passwordEncoder.encode(trainer.getPassword()));

        Trainer savedTrainer = trainerDAO.create(trainer);

        return savedTrainer.toBuilder().password(oldPassword).build();
    }

    @Override
    public Trainer select(Long id) {
        return trainerDAO.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Trainer not found with id = " + id));
    }

    private void checkAvailableUserName(Trainer trainer) {
        userDao.getByUsername(trainer.getUsername())
                .ifPresent(foundTrainee -> {
                    trainer.setUsername(trainer.getUsername() + UUID.randomUUID());
                });
    }

    private String constructTrainerUsername(Trainer trainer) {
        checkFirstLastNames(trainer);

        return trainer.getFirstName() + "." + trainer.getLastName();
    }

    private void checkFirstLastNames(Trainer trainer) {
        Objects.requireNonNull(trainer, "Trainer can't be null");
        Objects.requireNonNull(trainer.getFirstName(), "Trainer's firstName can't be null");
        Objects.requireNonNull(trainer.getLastName(), "Trainer's lastName can't be null");
    }

    private void checkNames(Trainer trainer) {
        checkFirstLastNames(trainer);
        Objects.requireNonNull(trainer.getUsername(), "Trainer.username can't be null");
    }

    @Override
    public boolean isUserNameMatchPassword(String username, String password) {
        Trainer foundTrainer = trainerDAO.getByUsername(username)
                .orElseThrow(() -> new BusinessLogicException("Trainer not found " + username));

        return password != null && password.equals(foundTrainer.getPassword());
    }

    @Transactional
    @Override
    public Trainer changePassword(Trainer trainer, String newPassword) {
        Trainer foundTrainer = select(trainer.getId());

        if (!foundTrainer.getPassword().equals(trainer.getPassword())) {
            throw new BusinessLogicException("Wrong password for trainer " + trainer.getUsername());
        }

        foundTrainer.setPassword(newPassword);

        return trainerDAO.update(foundTrainer);
    }

    @Transactional
    @Override
    public Trainer activateTrainer(Long trainerId) {
        Trainer foundTrainer = select(trainerId);

        if (foundTrainer.isActive()) {
            throw new BusinessLogicException("Trainer is already active " + foundTrainer);
        }

        foundTrainer.setActive(true);

        return trainerDAO.update(foundTrainer);
    }

    @Transactional
    @Override
    public Trainer deactivateTrainer(Long trainerId) {
        Trainer foundTrainer = select(trainerId);

        if (!foundTrainer.isActive()) {
            throw new BusinessLogicException("Trainer is already deactivated " + foundTrainer);
        }

        foundTrainer.setActive(false);

        return trainerDAO.update(foundTrainer);
    }

    @Override
    public List<Training> getTrainingsByDate(LocalDate startDate, LocalDate endDate, String userName) {
        return trainerDAO.getTrainingsByDateAndUsername(startDate, endDate, userName);
    }

    @Transactional
    @Override
    public List<Trainer> getTrainersWithoutPassedTrainee(String traineeUsername, Pageable pageable) {
        Trainee trainee = traineeDAO.getByUsername(traineeUsername)
                .orElseThrow(() -> new BusinessLogicException("Trainee not found " + traineeUsername));

        return trainerDAO.getTrainersWithoutPassedTrainee(trainee, pageable);
    }

    @Override
    public List<Trainer> getAll() {
        return trainerDAO.getAll();
    }

    @Override
    public Trainer authenticateTrainer(String userName, String password) {
        Trainer foundTrainer = trainerDAO.getByUsername(userName)
                .orElseThrow(() -> new BusinessLogicException("Trainer not found " + userName));

        if (!foundTrainer.getPassword().equals(password)) {
            throw new BusinessLogicException("Wrong password for trainer " + userName);
        }

        return foundTrainer;
    }

    @Override
    public Trainer getByUsername(String trainerUserName) {
        return trainerDAO.getByUsername(trainerUserName)
                .orElseThrow(() -> new NoSuchElementException("Trainer not found " + trainerUserName));
    }

    @Transactional
    @Override
    public Trainer getByUserNameWithTrainees(String trainerUsername) {
        Trainer trainer = getByUsername(trainerUsername);

        trainer.setTrainees(trainerDAO.getTraineesByTrainerUsername(trainer.getUsername()));

        return trainer;
    }

    @Transactional
    @Override
    public void activateDeactivateTrainer(String trainerUsername, boolean active) {
        Trainer trainer = getByUsername(trainerUsername);

        if (!active) {
            deactivateTrainer(trainer.getId());
        } else {
            activateTrainer(trainer.getId());
        }
    }
}
