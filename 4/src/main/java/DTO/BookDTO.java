package DTO;

import model.BookStatus;
import model.impl.Book;

import java.time.LocalDate;

public record BookDTO(
        Long id,
        String name,
        String author,
        Double price,
        Integer publicationDate,
        Integer amount,
        LocalDate lastDeliveredDate,
        LocalDate lastSaleDate,
        BookStatus status) {
    public BookDTO(Book book) {
        this(
                book.getId(),
                book.getName(),
                book.getAuthor(),
                book.getPrice(),
                book.getPublicationDate(),
                book.getAmount(),
                book.getLastDeliveredDate(),
                book.getLastSaleDate(),
                book.getStatus()
        );
    }
}