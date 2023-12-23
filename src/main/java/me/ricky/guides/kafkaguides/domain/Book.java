package me.ricky.guides.kafkaguides.domain;

public record Book(
    Integer bookId,
    String bookName,
    String bookAuthor
) {
}
