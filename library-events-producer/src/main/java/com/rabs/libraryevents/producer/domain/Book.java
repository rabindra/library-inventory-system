package com.rabs.libraryevents.producer.domain;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {
    @NotNull
    private Integer bookId;

    @NotBlank
    private String bookName;

    @NotBlank
    private String bookAuthor;

    private String bookISBN;

}
