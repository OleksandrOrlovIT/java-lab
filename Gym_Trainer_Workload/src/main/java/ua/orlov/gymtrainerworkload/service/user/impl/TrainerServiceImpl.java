package ua.orlov.gymtrainerworkload.service.user.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.orlov.gymtrainerworkload.dto.TrainerWorkload;
import ua.orlov.gymtrainerworkload.mapper.TrainerMapper;
import ua.orlov.gymtrainerworkload.model.ActionType;
import ua.orlov.gymtrainerworkload.model.MonthSummary;
import ua.orlov.gymtrainerworkload.model.Trainer;
import ua.orlov.gymtrainerworkload.model.YearSummary;
import ua.orlov.gymtrainerworkload.repository.TrainerRepository;
import ua.orlov.gymtrainerworkload.service.user.TrainerService;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainerMapper trainerMapper;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        return trainerRepository.save(trainer);
    }

    @Override
    public Trainer findByUsername(String username) {
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Trainer doesn't exist with username = " + username));
    }

    @Override
    @Transactional
    public void changeTrainerWorkload(TrainerWorkload trainerWorkload) {
        if (trainerWorkload.getActionType().equals(ActionType.ADD)) {
            if (!trainerExistsByUsername(trainerWorkload.getTrainerUsername())) {
                createTrainer(trainerMapper.trainerWorkloadToTrainer(trainerWorkload));
            } else {
                addTrainerWorkload(trainerWorkload);
            }
        } else {
            deleteTrainerWorkload(trainerWorkload);
        }
    }

    @Override
    public boolean trainerExistsByUsername(String username) {
        return trainerRepository.existsById(username);
    }

    private void addTrainerWorkload(TrainerWorkload trainerWorkload) {
        Trainer trainer = findByUsername(trainerWorkload.getTrainerUsername());

        int year = trainerWorkload.getTrainingDate().getYear();
        int month = trainerWorkload.getTrainingDate().getMonthValue();

        YearSummary yearSummary = trainer.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    YearSummary newYear = new YearSummary(year, new ArrayList<>());
                    trainer.getYears().add(newYear);
                    return newYear;
                });

        MonthSummary monthSummary = yearSummary.getMonths().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    MonthSummary newMonth = new MonthSummary(month, 0);
                    yearSummary.getMonths().add(newMonth);
                    return newMonth;
                });

        monthSummary.setDuration(monthSummary.getDuration() + trainerWorkload.getTrainingDurationMinutes());


        trainerRepository.save(trainer);
    }

    private void deleteTrainerWorkload(TrainerWorkload trainerWorkload) {
        Trainer trainer = findByUsername(trainerWorkload.getTrainerUsername());

        int year = trainerWorkload.getTrainingDate().getYear();
        int month = trainerWorkload.getTrainingDate().getMonthValue();

        YearSummary yearSummary = trainer.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Year not found for the trainer for = " + year));

        MonthSummary monthSummary = yearSummary.getMonths().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Month not found for the trainer for = " + month));

        int updatedDuration = monthSummary.getDuration() - trainerWorkload.getTrainingDurationMinutes();

        if (updatedDuration < 0) {
            throw new IllegalArgumentException("Training duration cannot be negative");
        }

        if (updatedDuration == 0) {
            yearSummary.getMonths().remove(monthSummary);
        } else {
            monthSummary.setDuration(updatedDuration);
        }

        if (yearSummary.getMonths().isEmpty()) {
            trainer.getYears().remove(yearSummary);
        }

        if(trainer.getYears().isEmpty()) {
            trainerRepository.delete(trainer);
        } else {
            trainerRepository.save(trainer);
        }
    }

}
