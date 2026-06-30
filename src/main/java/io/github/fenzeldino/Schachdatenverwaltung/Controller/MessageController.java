package io.github.fenzeldino.Schachdatenverwaltung.Controller;

import io.github.fenzeldino.Schachdatenverwaltung.Producer.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    private final MessageProducer messageProducer;

    @Autowired
    public MessageController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    // Endpunkt: http://localhost:8080/send?msg=Hallo
    @GetMapping("/send")
    public String sendMessage(@RequestParam("msg") String message) {
        messageProducer.sendMessage(message);
        return "Nachricht erfolgreich an RabbitMQ gesendet: " + message;
    }
}
