package ua.orlov.gymtrainerworkload.mapper;

import org.springframework.stereotype.Component;
import ua.orlov.gymtrainerworkload.dto.TrainerWorkload;
import ua.orlov.gymtrainerworkload.model.MonthSummary;
import ua.orlov.gymtrainerworkload.model.Trainer;
import ua.orlov.gymtrainerworkload.model.YearSummary;

import java.util.List;


@Component
public class TrainerMapper {

    public Trainer trainerWorkloadToTrainer(TrainerWorkload trainerWorkload) {
        Trainer trainer = new Trainer();
        trainer.setUsername(trainerWorkload.getTrainerUsername());
        trainer.setFirstName(trainerWorkload.getTrainerFirstName());
        trainer.setLastName(trainerWorkload.getTrainerLastName());
        trainer.setStatus(trainerWorkload.isTrainerIsActive());

        MonthSummary monthSummary = new MonthSummary();
        monthSummary.setMonth(trainerWorkload.getTrainingDate().getMonthValue());
        monthSummary.setDuration(trainerWorkload.getTrainingDurationMinutes());

        YearSummary yearSummary = new YearSummary();
        yearSummary.setYear(trainerWorkload.getTrainingDate().getYear());
        yearSummary.setMonths(List.of(monthSummary));

        trainer.setYears(List.of(yearSummary));

        return trainer;
    }

}
