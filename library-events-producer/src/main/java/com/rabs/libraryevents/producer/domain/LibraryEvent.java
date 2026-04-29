package com.rabs.libraryevents.producer.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LibraryEvent {
    private Integer libraryEventId;

    private LibraryEventType libraryEventType;
    @NotNull
    @Valid
    private Book book;


}
