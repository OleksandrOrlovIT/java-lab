package ua.orlov.gymtrainerworkload.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.orlov.gymtrainerworkload.dto.TrainerWorkload;
import ua.orlov.gymtrainerworkload.model.Month;
import ua.orlov.gymtrainerworkload.model.MonthSummary;
import ua.orlov.gymtrainerworkload.model.Trainer;
import ua.orlov.gymtrainerworkload.model.YearSummary;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainerMapperTest {

    private static final String USERNAME = "USERNAME";
    private static final String FIRST_NAME = "FIRST";
    private static final String LAST_NAME = "FIRST";

    private TrainerMapper trainerMapper;

    @BeforeEach
    void setUp() {
        trainerMapper = new TrainerMapper();
    }

    @Test
    void trainerWorkloadToTrainerThenSuccess() {
        List<YearSummary> yearSummaries = List.of(
                new YearSummary(2020, List.of(new MonthSummary(Month.fromOrder(1), 1)))
        );

        int duration = 1;

        TrainerWorkload workload = new TrainerWorkload();
        workload.setTrainerUsername(USERNAME);
        workload.setTrainerFirstName(FIRST_NAME);
        workload.setTrainerLastName(LAST_NAME);
        workload.setTrainerIsActive(true);
        workload.setTrainingDurationMinutes(duration);
        workload.setTrainingDate(LocalDate.of(2020, 1, 1));

        Trainer resultTrainer = trainerMapper.trainerWorkloadToTrainer(workload);

        assertAll("All mapping worked correctly",
                () -> assertEquals(USERNAME, resultTrainer.getUsername()),
                () -> assertEquals(FIRST_NAME, resultTrainer.getFirstName()),
                () -> assertEquals(LAST_NAME, resultTrainer.getLastName()),
                () -> assertTrue(resultTrainer.isStatus()),
                () -> assertEquals(yearSummaries, resultTrainer.getYears()));
    }
}
