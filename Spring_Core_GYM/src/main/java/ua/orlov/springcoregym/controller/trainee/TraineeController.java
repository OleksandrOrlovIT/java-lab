package ua.orlov.springcoregym.controller.trainee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.orlov.springcoregym.dto.trainee.*;
import ua.orlov.springcoregym.dto.trainer.TrainerResponse;
import ua.orlov.springcoregym.dto.user.UsernameIsActiveUser;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.dto.user.UsernameUser;
import ua.orlov.springcoregym.mapper.trainee.TraineeMapper;
import ua.orlov.springcoregym.mapper.traineetrainer.TraineeTrainerMapper;
import ua.orlov.springcoregym.mapper.trainer.TrainerMapper;
import ua.orlov.springcoregym.mapper.user.UserMapper;
import ua.orlov.springcoregym.model.user.Trainee;
import ua.orlov.springcoregym.model.user.Trainer;
import ua.orlov.springcoregym.security.annotations.user.IsSelf;
import ua.orlov.springcoregym.service.user.trainee.TraineeService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/trainee")
@AllArgsConstructor
public class TraineeController {

    private final TraineeService traineeService;
    private final TraineeMapper traineeMapper;
    private final TraineeTrainerMapper traineeTrainerMapper;
    private final UserMapper userMapper;
    private final TrainerMapper trainerMapper;

    @Operation(summary = "Register a new trainee", description = "Registers a new trainee in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered trainee",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsernamePasswordUser.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @PostMapping("/create")
    public ResponseEntity<UsernamePasswordUser> registerTrainee(@RequestBody @Validated TraineeRegister traineeRegister) {
        Trainee trainee = traineeService.create(traineeMapper.traineeRegisterToTrainee(traineeRegister));
        UsernamePasswordUser user = traineeMapper.traineeToUsernamePasswordUser(trainee);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get trainee by username",
            description = "Fetches details of a trainee including their assigned trainers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainee details",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TraineeFullResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @IsSelf
    @PostMapping("/username")
    public ResponseEntity<TraineeFullResponse> getTraineeByUsername(@RequestBody @Validated UsernameUser request) {
        Trainee trainee = traineeService.getByUserNameWithTrainers(request.getUsername());

        return ResponseEntity.ok(traineeTrainerMapper.traineeToTraineeFullResponse(trainee));
    }

    @Operation(summary = "Update trainee details", description = "Updates the details of an existing trainee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainee",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TraineeFullUsernameResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @IsSelf
    @PutMapping
    public ResponseEntity<TraineeFullUsernameResponse> updateTrainee(@RequestBody @Validated UpdateTraineeRequest request) {
        Trainee trainee = traineeService.update(traineeMapper.updateTraineeRequestToTrainee(request));

        return ResponseEntity.ok(traineeTrainerMapper.traineeToTraineeFullUsernameResponse(trainee));
    }

    @Operation(summary = "Delete trainee by username",
            description = "Deletes a trainee from the system based on their username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted trainee"),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @IsSelf
    @DeleteMapping
    public ResponseEntity<?> deleteTraineeByUsername(@RequestBody @Validated UsernameUser request) {
        traineeService.deleteByUsername(request.getUsername());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update trainee's list of trainers",
            description = "Updates the list of trainers assigned to a specific trainee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated list of trainers",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @IsSelf
    @PutMapping("/trainers")
    public List<TrainerResponse> updateTraineeTrainersList(@RequestBody @Validated UpdateTraineeTrainersListRequest request) {
        List<Trainer> trainers = traineeService.updateTraineeTrainers(request.getUsername(),
                userMapper.mapUsernameUserListToStringList(request.getTrainers()));

        return trainerMapper.trainersListToTrainerResponseList(trainers);
    }

    @Operation(summary = "Activate or deactivate trainee", description = "Activates or deactivates a trainee's account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee account activation/deactivation successful"),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @IsSelf
    @PatchMapping("/active")
    public ResponseEntity<?> activateDeactivateTrainee(@RequestBody @Validated UsernameIsActiveUser request){
        traineeService.activateDeactivateTrainee(request.getUsername(), request.getIsActive());

        return ResponseEntity.ok().build();
    }
}
