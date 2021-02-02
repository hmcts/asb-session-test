package uk.gov.hmcts.reform.demo.config;

import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

@Component
public class CcdEventMessageConsumer {

    @Value("${azure.host}")
    private String host;
    @Value("${azure.topic}")
    private String topic;
    @Value("${azure.subscription}")
    private String subscription;

    @Bean
    public Consumer<ServiceBusReceivedMessageContext> receiveMessageWithSession() {
        Consumer<ServiceBusReceivedMessageContext> onMessage = context -> {
            ServiceBusReceivedMessage message = context.getMessage();
            //processor.processMesssage(new String(message.getBody().toBytes()));
            processMessage(message);
            String sessionId = context.getMessage().getSessionId();

            Map<String, Object> applicationProperties = context.getMessage().getApplicationProperties();
            applicationProperties.forEach((key, value) -> System.out.println("key : "+key + "value : "+value));
        };
        return onMessage;
    }

    @Bean
    public Consumer<ServiceBusErrorContext> errorHandler() {
        return context -> System.out.println("exception...............:"+context.getException());
    }

    private static boolean processMessage(ServiceBusReceivedMessage message) {
        System.out.printf("Session: %s. Sequence #: %s. Contents: %s%n", message.getSessionId(),
                          message.getSequenceNumber(), message.getBody());
        return true;
    }

}
