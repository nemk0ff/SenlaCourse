package DTO;

import model.BookStatus;

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
}