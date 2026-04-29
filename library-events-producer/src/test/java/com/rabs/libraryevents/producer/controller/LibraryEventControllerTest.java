package com.rabs.libraryevents.producer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabs.libraryevents.producer.domain.Book;
import com.rabs.libraryevents.producer.domain.LibraryEvent;
import com.rabs.libraryevents.producer.domain.LibraryEventType;
import com.rabs.libraryevents.producer.producer.LibraryEventsProducer;
import com.rabs.libraryevents.producer.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventController.class)
@AutoConfigureMockMvc
class LibraryEventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    LibraryEventsProducer libraryEventsProducer;

    @Test
    void postLibraryEvent() throws Exception {
        //given
        LibraryEvent libraryEvent = TestUtil.getLibraryEvent();
        String libraryEventJson = objectMapper.writeValueAsString(libraryEvent);

        //when
        when(libraryEventsProducer.sendLibraryEvent(isA(LibraryEvent.class))).thenReturn(null);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/libraryevent")
                        .content(libraryEventJson)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

    }

    @Test
    void postLibraryEvent_invalidValues() throws Exception {
        //given
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .libraryEventType(LibraryEventType.NEW)
                .book(Book.builder()
                        .bookId(null)
                        .bookName("")
                        .bookAuthor("")
                        .build())
                .build();

        String libraryEventJson = objectMapper.writeValueAsString(libraryEvent);

        //when
        when(libraryEventsProducer.sendLibraryEvent(isA(LibraryEvent.class))).thenReturn(null);

        //expect
        String expectedErrorMessage = "book.bookAuthor - must not be blank, book.bookId - must not be null, book.bookName - must not be blank";
        mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/libraryevent")
                        .content(libraryEventJson)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError())
        .andExpect(content().string(expectedErrorMessage));

    }


}
