package ua.orlov.springcoregym.dto.trainer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTrainerRequest {

    @NotBlank(message = "username is required.")
    private String username;

    @NotBlank(message = "firstName is required.")
    private String firstName;

    @NotBlank(message = "lastName is required.")
    private String lastName;

    private Long specializationId;

    @JsonProperty("isActive")
    private boolean isActive;
}
