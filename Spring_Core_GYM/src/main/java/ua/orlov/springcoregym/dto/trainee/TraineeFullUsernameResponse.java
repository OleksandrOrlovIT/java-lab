package ua.orlov.springcoregym.dto.trainee;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.orlov.springcoregym.dto.trainer.TrainerResponse;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TraineeFullUsernameResponse {

    private String username;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String address;

    @JsonProperty("active")
    private boolean active;

    private List<TrainerResponse> trainers;
}
