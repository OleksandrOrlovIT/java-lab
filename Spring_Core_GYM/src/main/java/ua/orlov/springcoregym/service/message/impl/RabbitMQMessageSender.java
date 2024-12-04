package ua.orlov.springcoregym.service.message.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.orlov.springcoregym.dto.trainer.TrainerWorkload;
import ua.orlov.springcoregym.service.message.MessageSender;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
public class RabbitMQMessageSender implements MessageSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String rabbitmqTrainerWorkloadQueueName;

    public RabbitMQMessageSender(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper,
                                 @Value("${rabbitmq.trainer-workload-queue-name}") String rabbitmqTrainerWorkloadQueueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.rabbitmqTrainerWorkloadQueueName = rabbitmqTrainerWorkloadQueueName;
    }

    @Override
    public void sendMessage(String message, String json, String queueName) throws JsonProcessingException {
        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("subject", message);
        messageContent.put("content", json);

        String jsonMessage = objectMapper.writeValueAsString(messageContent);
        rabbitTemplate.convertAndSend(queueName, jsonMessage);
    }

    @Override
    public void sendMessageToTrainerWorkload(TrainerWorkload trainerWorkload) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(trainerWorkload);
        sendMessage("Trainer workload", json, rabbitmqTrainerWorkloadQueueName);
    }
}
