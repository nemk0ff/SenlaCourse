package DTO;

import managers.LibraryManager;

import java.util.Map;
import java.util.stream.Collectors;

public record LibraryManagerDTO(Map<Long, BookDTO> books) {
    public LibraryManagerDTO(LibraryManager libraryManager) {
        this(libraryManager.getBooks().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new BookDTO(entry.getValue()))));
    }
}
