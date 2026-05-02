package com.rabs.libraryevents.consumer.service;


import com.rabs.libraryevents.consumer.entity.LibraryEvent;
import com.rabs.libraryevents.consumer.repository.LibraryEventsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class LibraryEventsService {

    private LibraryEventsRepository libraryEventsRepository;
    private ObjectMapper objectMapper;

    public LibraryEventsService(LibraryEventsRepository libraryEventsRepository, ObjectMapper objectMapper) {
        this.libraryEventsRepository = libraryEventsRepository;
        this.objectMapper = objectMapper;
    }

    public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) {
        LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
        log.info("libraryEvent: {}", libraryEvent);

        switch (libraryEvent.getLibraryEventType()) {
            case NEW -> {
                libraryEvent.getBook().setLibraryEvent(libraryEvent);
                libraryEventsRepository.save(libraryEvent);
                log.info("libraryEvent saved");
            }
            case UPDATE -> {
                if(libraryEvent.getLibraryEventId() == null) {
                    throw new IllegalArgumentException("Library Event Id is missing");
                }

                var existingLibraryEvent = libraryEventsRepository.findById(libraryEvent.getLibraryEventId());
                if(!existingLibraryEvent.isPresent()) {
                    throw new IllegalArgumentException("Not a valid library event");
                }

                existingLibraryEvent.ifPresent(currentLibraryEvent -> {

                    libraryEvent.getBook().setLibraryEvent(libraryEvent);
                    libraryEventsRepository.save(libraryEvent);
                    log.info("libraryEvent updated");
                });
            }
        }

    }
}
