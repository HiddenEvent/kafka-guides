package me.ricky.guides.kafkaguides.domain;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

// recode는 Java 14부터 지원하는 기능입니다.
public record LibraryEvent(
        Integer libraryEventId,
        LibraryEventType libraryEventType,
        @NotNull
        @Valid
        Book book
) {
}
