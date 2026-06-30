package io.github.fenzeldino.Schachdatenverwaltung.Producer;
import io.github.fenzeldino.Schachdatenverwaltung.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageProducer(RabbitTemplate rabbitTemplate) {
        // Das RabbitTemplate wird automatisch von Spring Boot injiziert
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        // Sendet die Nachricht direkt an die konfigurierte Queue
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, message);
        System.out.println(" [x] Sent '" + message + "'");
    }
}
