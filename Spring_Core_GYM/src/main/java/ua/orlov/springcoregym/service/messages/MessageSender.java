package ua.orlov.springcoregym.service.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import ua.orlov.springcoregym.dto.trainer.TrainerWorkload;

public interface MessageSender {

    void sendMessage(String message, String json, String queueName) throws JsonProcessingException;

    void sendMessageToTrainerWorkload(TrainerWorkload trainerWorkload) throws JsonProcessingException;
}
