package com.rabs.libraryevents.producer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabs.libraryevents.producer.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class LibraryEventsProducer {

    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.name}")
    private String topic;

    public LibraryEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<Integer,String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        // simply call kafkaTemplate.send(topic, key, value) for without header information
        CompletableFuture<SendResult<Integer, String>> completableFuture = kafkaTemplate.send(new ProducerRecord<>(topic, null, key, value, List.of(new RecordHeader("event-source","scanner".getBytes()))));

        return completableFuture.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message Sent Successfully for the key : {} and the value is {} , partition is {}", key, value, result.getRecordMetadata().partition());
            } else {
                log.error("Error Sending the Message and the exception is {}", ex.getMessage());
            }
        });
    }
}
