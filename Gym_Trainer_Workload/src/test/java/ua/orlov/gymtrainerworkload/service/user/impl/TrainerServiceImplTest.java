package ua.orlov.gymtrainerworkload.service.user.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.orlov.gymtrainerworkload.dto.TrainerWorkload;
import ua.orlov.gymtrainerworkload.exception.BusinessLogicException;
import ua.orlov.gymtrainerworkload.mapper.TrainerMapper;
import ua.orlov.gymtrainerworkload.model.*;
import ua.orlov.gymtrainerworkload.repository.TrainerRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceImplTest {

    private static final String USERNAME = "USERNAME";

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainerServiceImpl trainerServiceImpl;

    @Test
    void createTrainerThenSuccess() {
        when(trainerRepository.save(any())).thenReturn(new Trainer());

        assertNotNull(trainerServiceImpl.createTrainer(new Trainer()));

        verify(trainerRepository, times(1)).save(any());
    }

    @Test
    void findByUsernameThenException() {
        when(trainerRepository.findByUsername(any())).thenReturn(Optional.empty());

        var e = assertThrows(NoSuchElementException.class, () -> trainerServiceImpl.findByUsername(USERNAME));
        assertEquals("Trainer doesn't exist with username = " + USERNAME, e.getMessage());
    }

    @Test
    void findByUsernameThenSuccess() {
        when(trainerRepository.findByUsername(any())).thenReturn(Optional.of(new Trainer()));

        assertNotNull(trainerServiceImpl.findByUsername(USERNAME));

        verify(trainerRepository, times(1)).findByUsername(any());
    }

    @Test
    void trainerExistsByIdThenSuccess() {
        when(trainerRepository.existsById(any())).thenReturn(true);

        assertTrue(trainerServiceImpl.trainerExistsByUsername(USERNAME));

        verify(trainerRepository, times(1)).existsById(any());
    }

    @Test
    void changeTrainerWorkloadThenCreateTrainer() {
        when(trainerRepository.existsById(any())).thenReturn(false);
        when(trainerRepository.save(any())).thenReturn(new Trainer());
        when(trainerMapper.trainerWorkloadToTrainer(any())).thenReturn(new Trainer());

        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainingDate(LocalDate.of(2020, 10, 10));
        trainerWorkload.setActionType(ActionType.ADD);
        trainerWorkload.setTrainerActive(true);
        trainerWorkload.setTrainingDurationMinutes(1);

        trainerServiceImpl.changeTrainerWorkload(trainerWorkload);

        verify(trainerRepository).existsById(any());
        verify(trainerRepository).save(any());
        verify(trainerMapper).trainerWorkloadToTrainer(any());
    }

    @Test
    void changeTrainerWorkloadThenAddTrainerWorkloadWithoutYear() {
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainingDate(LocalDate.of(2020, 10, 10));
        trainerWorkload.setActionType(ActionType.ADD);
        trainerWorkload.setTrainingDurationMinutes(1);

        Trainer trainer = new Trainer();
        List<MonthSummary> monthSummaries = new ArrayList<>();
        monthSummaries.add(new MonthSummary(Month.fromOrder(11), 1));

        List<YearSummary> yearSummaries = new ArrayList<>();
        yearSummaries.add(new YearSummary(2019, monthSummaries));

        trainer.setYears(yearSummaries);

        when(trainerRepository.existsById(any())).thenReturn(true);
        when(trainerRepository.save(any())).thenReturn(new Trainer());
        when(trainerRepository.findByUsername(any())).thenReturn(Optional.of(trainer));

        trainerServiceImpl.changeTrainerWorkload(trainerWorkload);

        verify(trainerRepository).existsById(any());
        verify(trainerRepository).save(any());
        verify(trainerRepository).findByUsername(any());
    }

    @Test
    void changeTrainerWorkloadThenAddTrainerWorkloadWithYearWithoutMonth() {
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainingDate(LocalDate.of(2020, 10, 10));
        trainerWorkload.setActionType(ActionType.ADD);
        trainerWorkload.setTrainingDurationMinutes(1);

        Trainer trainer = new Trainer();
        List<MonthSummary> monthSummaries = new ArrayList<>();
        monthSummaries.add(new MonthSummary(Month.fromOrder(11), 1));

        List<YearSummary> yearSummaries = new ArrayList<>();
        yearSummaries.add(new YearSummary(2020, monthSummaries));

        trainer.setYears(yearSummaries);

        when(trainerRepository.existsById(any())).thenReturn(true);
        when(trainerRepository.save(any())).thenReturn(new Trainer());
        when(trainerRepository.findByUsername(any())).thenReturn(Optional.of(trainer));

        trainerServiceImpl.changeTrainerWorkload(trainerWorkload);

        verify(trainerRepository).existsById(any());
        verify(trainerRepository).save(any());
        verify(trainerRepository).findByUsername(any());
    }

    @Test
    void changeTrainerWorkloadThenAddTrainerWorkloadWithMonth() {
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainingDate(LocalDate.of(2020, 10, 10));
        trainerWorkload.setActionType(ActionType.ADD);
        trainerWorkload.setTrainingDurationMinutes(1);

        Trainer trainer = new Trainer();
        List<MonthSummary> monthSummaries = new ArrayList<>();
        monthSummaries.add(new MonthSummary(Month.fromOrder(10), 1));

        List<YearSummary> yearSummaries = new ArrayList<>();
        yearSummaries.add(new YearSummary(2020, monthSummaries));

        trainer.setYears(yearSummaries);

        when(trainerRepository.existsById(any())).thenReturn(true);
        when(trainerRepository.save(any())).thenReturn(new Trainer());
        when(trainerRepository.findByUsername(any())).thenReturn(Optional.of(trainer));

        trainerServiceImpl.changeTrainerWorkload(trainerWorkload);

        verify(trainerRepository).existsById(any());
        verify(trainerRepository).save(any());
        verify(trainerRepository).findByUsername(any());
    }

    @Test
    void changeTrainerWorkloadThenDeleteTrainerWorkloadWithoutYearThenException() {
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainingDate(LocalDate.of(2020, 10, 10));
        trainerWorkload.setActionType(ActionType.DELETE);

        Trainer trainer = new Trainer();

        trainer.setYears(new ArrayList<>());

        when(trainerRepository.findByUsername(any())).thenReturn(Optional.of(trainer));

        var e = assertThrows(BusinessLogicException.class, () -> trainerServiceImpl.changeTrainerWorkload(trainerWorkload));

        assertEquals("Year not found for the trainer for = 2020", e.getMessage());

        verify(trainerRepository).findByUsername(any());
    }

    @Test
    void changeTrainerWorkloadThenDeleteTrainerWorkloadWithoutMonthThenException() {
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainingDate(LocalDate.of(2020, 10, 10));
        trainerWorkload.setActionType(ActionType.DELETE);

        Trainer trainer = new Trainer();
        List<MonthSummary> monthSummaries = new ArrayList<>();
        monthSummaries.add(new MonthSummary(Month.fromOrder(11), 1));

        List<YearSummary> yearSummaries = new ArrayList<>();
        yearSummaries.add(new YearSummary(2020, monthSummaries));

        trainer.setYears(yearSummaries);

        when(trainerRepository.findByUsername(any())).thenReturn(Optional.of(trainer));

        var e = assertThrows(BusinessLogicException.class, () -> trainerServiceImpl.changeTrainerWorkload(trainerWorkload));

        assertEquals("Month not found for the trainer for = 10", e.getMessage());

        verify(trainerRepository).findByUsername(any());
    }

    @Test
    void changeTrainerWorkloadThenDeleteTrainerWorkloadWithWrongDurationThenException() {
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainingDate(LocalDate.of(2020, 10, 10));
        trainerWorkload.setActionType(ActionType.DELETE);
        trainerWorkload.setTrainingDurationMinutes(2);

        Trainer trainer = new Trainer();
        List<MonthSummary> monthSummaries = new ArrayList<>();
        monthSummaries.add(new MonthSummary(Month.fromOrder(10), 1));

        List<YearSummary> yearSummaries = new ArrayList<>();
        yearSummaries.add(new YearSummary(2020, monthSummaries));

        trainer.setYears(yearSummaries);

        when(trainerRepository.findByUsername(any())).thenReturn(Optional.of(trainer));

        var e = assertThrows(BusinessLogicException.class, () -> trainerServiceImpl.changeTrainerWorkload(trainerWorkload));

        assertEquals("Training duration cannot be negative", e.getMessage());

        verify(trainerRepository).findByUsername(any());
    }

    @Test
    void changeTrainerWorkloadThenDeleteTrainerWorkloadDeletingYear() {
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainingDate(LocalDate.of(2020, 10, 10));
        trainerWorkload.setActionType(ActionType.DELETE);
        trainerWorkload.setTrainingDurationMinutes(1);

        Trainer trainer = new Trainer();
        List<MonthSummary> monthSummaries = new ArrayList<>();
        monthSummaries.add(new MonthSummary(Month.fromOrder(10), 1));

        List<YearSummary> yearSummaries = new ArrayList<>();
        yearSummaries.add(new YearSummary(2020, monthSummaries));

        trainer.setYears(yearSummaries);

        when(trainerRepository.findByUsername(any())).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> trainerServiceImpl.changeTrainerWorkload(trainerWorkload));

        verify(trainerRepository).findByUsername(any());
        verify(trainerRepository).delete(any());
    }

    @Test
    void changeTrainerWorkloadThenDeleteTrainerWorkloadDecreasingDuration() {
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainingDate(LocalDate.of(2020, 10, 10));
        trainerWorkload.setActionType(ActionType.DELETE);
        trainerWorkload.setTrainingDurationMinutes(1);

        Trainer trainer = new Trainer();
        List<MonthSummary> monthSummaries = new ArrayList<>();
        monthSummaries.add(new MonthSummary(Month.fromOrder(10), 2));


        List<YearSummary> yearSummaries = new ArrayList<>();
        yearSummaries.add(new YearSummary(2020, monthSummaries));

        trainer.setYears(yearSummaries);

        when(trainerRepository.findByUsername(any())).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any())).thenReturn(new Trainer());

        assertDoesNotThrow(() -> trainerServiceImpl.changeTrainerWorkload(trainerWorkload));

        verify(trainerRepository).findByUsername(any());
        verify(trainerRepository).save(any());
    }

    @Test
    void changeTrainerWorkloadThenDeleteTrainerWorkloadDecreasingDurationWithDifferentYears() {
        TrainerWorkload trainerWorkload = new TrainerWorkload();
        trainerWorkload.setTrainingDate(LocalDate.of(2020, 10, 10));
        trainerWorkload.setActionType(ActionType.DELETE);
        trainerWorkload.setTrainingDurationMinutes(1);

        Trainer trainer = new Trainer();
        List<MonthSummary> monthSummaries = new ArrayList<>();
        monthSummaries.add(new MonthSummary(Month.fromOrder(10), 2));


        List<YearSummary> yearSummaries = new ArrayList<>();
        yearSummaries.add(new YearSummary(2019, monthSummaries));
        yearSummaries.add(new YearSummary(2018, monthSummaries));
        yearSummaries.add(new YearSummary(2020, monthSummaries));

        trainer.setYears(yearSummaries);

        when(trainerRepository.findByUsername(any())).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any())).thenReturn(new Trainer());

        assertDoesNotThrow(() -> trainerServiceImpl.changeTrainerWorkload(trainerWorkload));

        verify(trainerRepository).findByUsername(any());
        verify(trainerRepository).save(any());
    }
}
