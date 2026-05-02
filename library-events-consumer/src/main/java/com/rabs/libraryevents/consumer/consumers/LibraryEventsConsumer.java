package com.rabs.libraryevents.consumer.consumers;

import com.rabs.libraryevents.consumer.service.LibraryEventsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.json.JsonParseException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class LibraryEventsConsumer {

    private LibraryEventsService libraryEventsService;

    public LibraryEventsConsumer(LibraryEventsService libraryEventsService){
        this.libraryEventsService = libraryEventsService;
    }

    @KafkaListener(topics = {"${spring.kafka.topic.name}"})
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord)  {
        log.info("Consumer Record: {}", consumerRecord);

        libraryEventsService.processLibraryEvent(consumerRecord);
    }
}