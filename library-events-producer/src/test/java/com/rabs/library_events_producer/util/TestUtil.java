package com.rabs.library_events_producer.util;

import com.rabs.library_events_producer.domain.Book;
import com.rabs.library_events_producer.domain.LibraryEvent;
import com.rabs.library_events_producer.domain.LibraryEventType;

public class TestUtil {

    public static LibraryEvent getLibraryEvent() {
        return new LibraryEvent(
                null,
                LibraryEventType.NEW,
                new Book().builder()
                        .bookId(102)
                        .bookName("Book Name goes here")
                        .bookAuthor("Rabindra")
                        .bookISBN("IBSN1234567")
                        .build()
        );
    }
}
