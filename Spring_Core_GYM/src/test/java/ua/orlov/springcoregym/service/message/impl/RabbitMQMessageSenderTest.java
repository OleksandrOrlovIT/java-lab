package ua.orlov.springcoregym.service.message.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import ua.orlov.springcoregym.dto.trainer.TrainerWorkload;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQMessageSenderTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private static final String QUEUE_NAME = "test_queue";

    private RabbitMQMessageSender rabbitMQMessageSender;

    @BeforeEach
    void setUp() {
        rabbitMQMessageSender = new RabbitMQMessageSender(rabbitTemplate, objectMapper, QUEUE_NAME);
    }

    @Test
    void sendMessageThenNothing() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("asd");

        assertDoesNotThrow(() -> rabbitMQMessageSender.sendMessage("", "", ""));

        verify(objectMapper, times(1)).writeValueAsString(any());
        verify(rabbitTemplate, times(1)).convertAndSend(any(String.class), any(Object.class));
    }

    @Test
    void sendMessageToTrainerWorkloadThenException() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenThrow(JsonParseException.class);

        assertThrows(JsonParseException.class, () -> rabbitMQMessageSender.sendMessageToTrainerWorkload(null));

        verify(objectMapper, times(1)).writeValueAsString(any());
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void sendMessageToTrainerWorkload() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenReturn("asd");

        rabbitMQMessageSender.sendMessageToTrainerWorkload(new TrainerWorkload());

        verify(objectMapper, times(2)).writeValueAsString(any());
        verify(rabbitTemplate, times(1)).convertAndSend(any(String.class), any(Object.class));
    }
}
