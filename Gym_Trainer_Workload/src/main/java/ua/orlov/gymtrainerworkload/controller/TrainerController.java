package ua.orlov.gymtrainerworkload.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.orlov.gymtrainerworkload.dto.TrainerWorkload;
import ua.orlov.gymtrainerworkload.model.Trainer;
import ua.orlov.gymtrainerworkload.service.user.TrainerService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/trainer")
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping("/workload")
    public ResponseEntity<String> changeWorkLoad(@RequestBody @Validated TrainerWorkload trainerWorkload) {
        System.out.println("Change workload controller");
        trainerService.changeTrainerWorkload(trainerWorkload);

        return ResponseEntity.ok("Workload changed");
    }

    @GetMapping("/summary/month")
    public Trainer getSummaryMonth(@RequestParam String username) {
        return trainerService.findByUsername(username);
    }

}
