package me.ricky.guides.kafkaguides.domain;



// recode는 Java 14부터 지원하는 기능입니다.
public record LibraryEvent(
    Integer libraryEventId,
    LibraryEventType libraryEventType,
    Book book
) {
}
