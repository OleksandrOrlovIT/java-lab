package ua.orlov.springcoregym.controller.trainer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.orlov.springcoregym.dto.trainer.*;
import ua.orlov.springcoregym.dto.user.UsernameIsActiveUser;
import ua.orlov.springcoregym.dto.user.UsernamePasswordUser;
import ua.orlov.springcoregym.dto.user.UsernameUser;
import ua.orlov.springcoregym.mapper.traineetrainer.TraineeTrainerMapper;
import ua.orlov.springcoregym.mapper.trainer.TrainerMapper;
import ua.orlov.springcoregym.model.user.Trainer;
import ua.orlov.springcoregym.security.annotations.user.IsSelf;
import ua.orlov.springcoregym.service.user.trainer.TrainerService;
import ua.orlov.springcoregym.model.page.Pageable;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/trainer")
@AllArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainerMapper trainerMapper;
    private final TraineeTrainerMapper traineeTrainerMapper;

    @Operation(summary = "Register a new trainer",
            description = "Registers a new trainer with the provided details and returns the username and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered trainer",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsernamePasswordUser.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @PostMapping
    public ResponseEntity<UsernamePasswordUser> registerTrainer(@Validated @RequestBody TrainerRegister trainerRegister) {
        Trainer trainer = trainerService.createFromTrainerRegister(trainerRegister);

        UsernamePasswordUser user = trainerMapper.trainerToUsernamePasswordUser(trainer);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get trainer by username",
            description = "Retrieves the details of a trainer along with their assigned trainees using the provided username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainer with trainees",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerFullResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @IsSelf
    @GetMapping
    public ResponseEntity<TrainerFullResponse> getTrainerByUsername(@RequestBody @Validated UsernameUser request) {
        Trainer trainer = trainerService.getByUserNameWithTrainees(request.getUsername());

        return ResponseEntity.ok(traineeTrainerMapper.trainerToTrainerFullResponse(trainer));
    }

    @Operation(summary = "Update trainer details",
            description = "Updates the details of a trainer based on the provided information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainer",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerFullUsernameResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @IsSelf
    @PutMapping
    public ResponseEntity<TrainerFullUsernameResponse> updateTrainer(@RequestBody @Validated UpdateTrainerRequest request) {
        Trainer trainer = trainerService.updateFromUpdateTrainerRequest(request);

        return ResponseEntity.ok(traineeTrainerMapper.trainerToTrainerFullUsernameResponse(trainer));
    }

    @Operation(summary = "Get trainers without a specified trainee",
            description = "Retrieves a list of trainers who are not assigned to the specified trainee, with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainers",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @IsSelf
    @GetMapping("/without-trainee")
    public List<TrainerResponse> getTrainersWithoutTrainee(@RequestBody @Validated UsernameUser request) {
        List<Trainer> foundTrainers = trainerService
                .getTrainersWithoutPassedTrainee(request.getUsername(), new Pageable(0, 10));

        return trainerMapper.trainersListToTrainerResponseList(foundTrainers);
    }

    @Operation(summary = "Activate or deactivate a trainer",
            description = "Activates or deactivates a trainer based on the provided username and active status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainer status",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "AccessDenied (e.g., AccessDeniedException)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    })
    @IsSelf
    @PatchMapping("/active")
    public ResponseEntity<?> activateDeactivateTrainer(@RequestBody @Validated UsernameIsActiveUser request){
        trainerService.activateDeactivateTrainer(request.getUsername(), request.isActive());

        return ResponseEntity.ok().build();
    }
}
