package io.github.fenzeldino.Schachdatenverwaltung.config;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "testQueue";

    @Bean
    public Queue testQueue() {
        // Erstellt eine durable (dauerhafte) Queue mit dem Namen "testQueue"
        return new Queue(QUEUE_NAME, true);
    }
}
