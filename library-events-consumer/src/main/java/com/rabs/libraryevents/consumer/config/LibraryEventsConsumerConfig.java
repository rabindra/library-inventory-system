package com.rabs.libraryevents.consumer.config;

import com.rabs.libraryevents.consumer.entity.FailureRecord;
import com.rabs.libraryevents.consumer.service.FailureRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.util.List;

@Configuration
//@EnableKafka
@Slf4j
public class LibraryEventsConsumerConfig {

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    FailureRecordService failureRecordService;

    @Value("${topics.retry}")
    private String retryTopic;

    @Value("${topics.dlt}")
    private String deadLetterTopic;

    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer() {
        DeadLetterPublishingRecoverer publishingRecoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (r, e) -> {
                    if (e instanceof RecoverableDataAccessException) {
                        return new TopicPartition(retryTopic, r.partition());
                    } else {
                        return new TopicPartition(deadLetterTopic, r.partition());
                    }
                });

        return  publishingRecoverer;
    }

    ConsumerRecordRecoverer consumerRecordRecoverer = (consumerRecord, ex) -> {
        log.info("Exception in consumerRecordRecoverer, Exception: {}, Exception: {}", ex.getMessage(), ex.getMessage());
        var failureRecord = (ConsumerRecord<Integer, String>) consumerRecord;
        if (ex instanceof RecoverableDataAccessException) {
            // Recovery
            this.failureRecordService.saveFailureRecord(failureRecord, ex, "RETRY");
        }else{
            // For non-recoverable
            this.failureRecordService.saveFailureRecord(failureRecord, ex, "DEAD");
        }
    };

    private DefaultErrorHandler errorHandler(){

        var listOfExceptionsToIgnore = List.of(IllegalArgumentException.class);

        // Fixed BackOff Attemps
        var fixedBackOff = new FixedBackOff(1000L, 2);

        // Exponential BackOff Attemps
        var exponentialBackOffRetires = new ExponentialBackOffWithMaxRetries(2);
        exponentialBackOffRetires.setInitialInterval(1_000L);
        exponentialBackOffRetires.setMultiplier(2.0);
        exponentialBackOffRetires.setMaxInterval(2_000L);

        var errorHandler  = new DefaultErrorHandler(
                //deadLetterPublishingRecoverer(),
                consumerRecordRecoverer,
                exponentialBackOffRetires
        ); //fixedBackOff);
        listOfExceptionsToIgnore.forEach(errorHandler::addNotRetryableExceptions);
        errorHandler.setRetryListeners((records, ex, deliveryAttempt) ->{
            log.info("Failed Record in Retry Listener, Exception: {}, deliveryAttempt: {}", ex.getMessage(), deliveryAttempt);
        });

        return errorHandler;
    }


    @Bean
    ConcurrentKafkaListenerContainerFactory<?,?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory
    ){

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);

        // Running consumer listener container in concurrent thread
        factory.setConcurrency(2);

        //custom error handler
        factory.setCommonErrorHandler(errorHandler());


        //To make manual ack
        //factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }
}
