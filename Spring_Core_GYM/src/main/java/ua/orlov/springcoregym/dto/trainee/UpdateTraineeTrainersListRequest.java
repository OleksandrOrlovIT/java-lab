package ua.orlov.springcoregym.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.orlov.springcoregym.dto.user.UsernameUser;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTraineeTrainersListRequest {

    @NotBlank(message = "username is required.")
    private String username;

    @NotEmpty(message = "trainers are required.")
    private List<UsernameUser> trainers;
}
