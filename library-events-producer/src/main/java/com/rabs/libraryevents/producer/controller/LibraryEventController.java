package com.rabs.libraryevents.producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabs.libraryevents.producer.domain.LibraryEvent;
import com.rabs.libraryevents.producer.domain.LibraryEventType;
import com.rabs.libraryevents.producer.producer.LibraryEventsProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("v1/libraryevent")
public class LibraryEventController {

    private final LibraryEventsProducer libraryEventsProducer;

    public LibraryEventController(LibraryEventsProducer libraryEventsProducer) {
        this.libraryEventsProducer = libraryEventsProducer;
    }

    @PostMapping("")
    public ResponseEntity<?> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException {
        log.info("Library event received | event : {}", libraryEvent);

        libraryEventsProducer.sendLibraryEvent(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }


    @PutMapping("")
    public ResponseEntity<?> updateLibraryEvent(
            @RequestBody @Valid LibraryEvent libraryEvent
    ) throws JsonProcessingException{
        log.info("Library event received | event : {}", libraryEvent);

        ResponseEntity<String> BAD_REQUEST = validateLibraryEvent(libraryEvent);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        libraryEventsProducer.sendLibraryEvent(libraryEvent);

        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

    private static ResponseEntity<String> validateLibraryEvent(LibraryEvent libraryEvent) {
        if(libraryEvent.getLibraryEventId()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("LibraryEventId cannot be empty!");
        }

        if(libraryEvent.getLibraryEventType() != LibraryEventType.UPDATE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Event Type should be pass UPDATE!");
        }
        return null;
    }
}
