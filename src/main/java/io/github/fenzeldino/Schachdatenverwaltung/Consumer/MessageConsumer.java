package io.github.fenzeldino.Schachdatenverwaltung.Consumer;
import io.github.fenzeldino.Schachdatenverwaltung.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    // Registriert diese Methode als Listener für die "testQueue"
    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        System.out.println(" [x] Received from RabbitMQ: " + message);
    }
}
