package ua.orlov.springcoregym.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final String DLQ_SUFFIX = ".dlq";

    @Value("${rabbitmq.trainer-workload-queue-name}")
    private String rabbitmqTrainerWorkloadQueueName;


    @Bean
    public ConnectionFactory connectionFactory(@Value("${rabbitmq.host}") String rabbitHost,
                                               @Value("${rabbitmq.port}") Integer rabbitPort,
                                               @Value("${rabbitmq.username}") String rabbitUsername,
                                               @Value("${rabbitmq.password}") String rabbitPassword) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitHost, rabbitPort);
        connectionFactory.setUsername(rabbitUsername);
        connectionFactory.setPassword(rabbitPassword);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public Queue trainerWorkloadQueue(@Value("${rabbitmq.retry-time-ms}") Integer retryTimeMs) {
        return QueueBuilder.durable(rabbitmqTrainerWorkloadQueueName)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", rabbitmqTrainerWorkloadQueueName + DLQ_SUFFIX)
                .withArgument("x-message-ttl", retryTimeMs)
                .build();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
