package me.ricky.guides.kafkaguides.domain;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.coyote.BadRequestException;

// recode는 Java 14부터 지원하는 기능입니다.
public record LibraryEvent(
        Integer libraryEventId,
        LibraryEventType libraryEventType,
        @NotNull
        @Valid
        Book book
) {
    public void validate() throws BadRequestException {
        if (this.libraryEventId == null)
            throw new BadRequestException("Please pass the LibraryEventId");

        if (this.libraryEventType != LibraryEventType.UPDATE)
            throw new BadRequestException("Please pass the LibraryEventType as UPDATE");

    }
}
