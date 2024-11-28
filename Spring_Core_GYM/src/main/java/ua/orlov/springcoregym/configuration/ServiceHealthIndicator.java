package ua.orlov.springcoregym.configuration;

import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ua.orlov.springcoregym.service.password.PasswordService;
import ua.orlov.springcoregym.service.training.TrainingService;
import ua.orlov.springcoregym.service.training.TrainingTypeService;
import ua.orlov.springcoregym.service.user.UserService;
import ua.orlov.springcoregym.service.user.trainee.TraineeService;
import ua.orlov.springcoregym.service.user.trainer.TrainerService;

@Component
@AllArgsConstructor
public class ServiceHealthIndicator implements HealthIndicator {

    private UserService userService;
    private PasswordService passwordService;
    private TrainingService trainingService;
    private TrainingTypeService trainingTypeService;
    private TraineeService traineeService;
    private TrainerService trainerService;

    @Override
    public Health health() {
        if (userService == null) {
            return Health.down().withDetail("Error", "UserService is unavailable").build();
        } else if (passwordService == null) {
            return Health.down().withDetail("Error", "PasswordService is unavailable").build();
        } else if (trainingService == null) {
            return Health.down().withDetail("Error", "TrainingService is unavailable").build();
        } else if (trainingTypeService == null) {
            return Health.down().withDetail("Error", "TrainingTypeService is unavailable").build();
        } else if (traineeService == null) {
            return Health.down().withDetail("Error", "TraineeService is unavailable").build();
        } else if (trainerService == null) {
            return Health.down().withDetail("Error", "TrainerService is unavailable").build();
        }

        return Health.up().build();
    }
}
