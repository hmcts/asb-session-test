package uk.gov.hmcts.reform.demo.config;

import com.azure.core.amqp.AmqpRetryOptions;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ServiceBusConfiguration {

    @Value("${azure.host}")
    private String host;
    @Value("${azure.topic}")
    private String topic;
    @Value("${azure.subscription}")
    private String subscription;

    private ServiceBusProcessorClient serviceBusClient;

    private  ServiceBusSenderClient sender;

    @Autowired
    private CcdEventMessageConsumer consumer;

    @Bean
    public void serviceBusProcessorClient() {
        AmqpRetryOptions amqpRetryOptions = new AmqpRetryOptions();
        amqpRetryOptions.setTryTimeout(Duration.ofSeconds(20));
        amqpRetryOptions.setDelay(Duration.ofDays(1));
        serviceBusClient = new ServiceBusClientBuilder()
            .connectionString(host)
            //.retryOptions(new AmqpRetryOptions().setTryTimeout(Duration.ofSeconds(20)))
            .sessionProcessor()
            .topicName(topic)
            .maxConcurrentSessions(2)
            .subscriptionName(subscription)
            .processMessage(consumer.receiveMessageWithSession())
            .processError(consumer.errorHandler())
            .buildProcessorClient();

        serviceBusClient.start();
    }

    @Bean
    public ServiceBusSenderClient createConnection() {
        sender = new ServiceBusClientBuilder()
            .connectionString(host)
            .sender()
            .topicName(topic)
            .buildClient();

        return sender;
    }

/*    @PreDestroy
    public void closeChannels() {
        sender.close();
        serviceBusClient.close();
    }*/

/*    @Bean
    public ServiceBusSessionReceiverAsyncClient serviceBusSessionReceiverConfig() {

        ServiceBusSessionReceiverAsyncClient sessionReceiver = new ServiceBusClientBuilder()
            .connectionString(host)
            .sessionReceiver()
            .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
            .maxAutoLockRenewDuration(Duration.ofDays(1))
            .topicName(topic)
            .subscriptionName(subscription)
            .buildAsyncClient();

        return sessionReceiver;
    }*/

/*    @Bean
    public ServiceBusSenderClient createConnection() {
        ServiceBusSenderClient sender = new ServiceBusClientBuilder()
            .connectionString(host)
            .sender()
            .topicName(topic)
            .buildClient();

        return sender;
    }*/

/*    @Bean
    public ServiceBusProcessorClient serviceBusProcessorClient() {
        ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
            .connectionString(host)
            .processor()
            .topicName(topic)
            .subscriptionName(subscription)
            //.processMessage(new TopicConsumer())
            //.processError(processError)
            .buildProcessorClient();

        return processorClient;
    }*/

/*    @Bean
    public void startProcessor(ServiceBusProcessorClient serviceBusProcessorClient) {
        serviceBusProcessorClient.start();
    }

    @Bean
    public void stopProcessor(ServiceBusProcessorClient serviceBusProcessorClient) {
        serviceBusProcessorClient.stop();
    }*/

}
