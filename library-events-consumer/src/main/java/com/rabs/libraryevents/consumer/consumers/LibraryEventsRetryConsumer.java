package com.rabs.libraryevents.consumer.consumers;


import com.rabs.libraryevents.consumer.service.LibraryEventsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LibraryEventsRetryConsumer {

    private final LibraryEventsService libraryEventsService;

    public LibraryEventsRetryConsumer(LibraryEventsService libraryEventsService) {
        this.libraryEventsService = libraryEventsService;
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = {"${topics.retry}"},
            autoStartup = "${retryListener.startup:true}",
            groupId = "retry-listener-group")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("Consumer Record in Retry Consumer: {}", consumerRecord);

        consumerRecord.headers().forEach(header -> {
            log.info("Header Key: {}, Header Value: {}", header.key(), new String(header.value()));
        });

        libraryEventsService.processLibraryEvent(consumerRecord);
    }

}
