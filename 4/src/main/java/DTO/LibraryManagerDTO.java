package DTO;

import managers.LibraryManager;

import java.util.List;
import java.util.stream.Collectors;

public record LibraryManagerDTO(List<BookDTO> books) {
    public LibraryManagerDTO(LibraryManager libraryManager) {
        this(libraryManager.getBooks().values().stream()
                .map(book -> new BookDTO(book.getId(),
                        book.getName(),
                        book.getAuthor(),
                        book.getPrice(),
                        book.getPublicationDate(),
                        book.getAmount(),
                        book.getLastDeliveredDate(),
                        book.getLastSaleDate(),
                        book.getStatus()))
                .collect(Collectors.toList()));
    }
}

