package ua.orlov.springcoregym.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeLoginDto {

    @NotBlank(message = "username is required.")
    private String username;

    @NotBlank(message = "oldPassword is required.")
    private String oldPassword;

    @NotBlank(message = "newPassword is required.")
    private String newPassword;
}
