package ua.orlov.springcoregym.security.training;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ua.orlov.springcoregym.model.user.User;
import ua.orlov.springcoregym.service.user.UserService;

@AllArgsConstructor
@Component
public class TrainingSecurity {

    private final UserService userService;

    public boolean trainingRequestHasLoggedUser(String traineeUsername, String trainerUsername) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User foundTrainee = null, foundTrainer = null;

        try {
            foundTrainee = userService.getByUsername(traineeUsername);
        } catch (Exception ignored) {
        }

        try {
            foundTrainer = userService.getByUsername(trainerUsername);
        } catch (Exception ignored) {
        }

        return (foundTrainee != null && foundTrainee.getUsername().equals(currentUsername))
                || (foundTrainer != null && foundTrainer.getUsername().equals(currentUsername));
    }
}
