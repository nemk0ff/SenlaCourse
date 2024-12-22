package DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import managers.LibraryManager;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class LibraryManagerDTO {
    private Map<Long, BookDTO> books;

    LibraryManagerDTO(LibraryManager libraryManager) {
        this.books = libraryManager.getBooks().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new BookDTO(entry.getValue())));
    }
}
