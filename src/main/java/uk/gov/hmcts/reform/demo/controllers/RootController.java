package uk.gov.hmcts.reform.demo.controllers;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Default endpoints per application.
 */
@RestController
public class RootController {

    @Autowired
    private ServiceBusSenderClient senderClient;

    /**
     * Root GET endpoint.
     *
     * <p>Azure application service has a hidden feature of making requests to root endpoint when
     * "Always On" is turned on.
     * This is the endpoint to deal with that and therefore silence the unnecessary 404s as a response code.
     *
     * @return Welcome message from the service.
     */
    @GetMapping("/")
    public ResponseEntity<String> welcome() {
        return ok("Welcome to spring-boot-template");
    }

    @GetMapping("/publish/{event}")
    public ResponseEntity<String> sendMessage(@PathVariable(name = "event") String event) {
        ServiceBusMessage message = new ServiceBusMessage("Message sent with event : "+event);
        message.setSessionId(event);
        senderClient.sendMessage(message);

        return ok("Message Sent...."+event);
    }
}
