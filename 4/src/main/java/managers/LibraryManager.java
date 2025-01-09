package managers;

import DTO.LibraryManagerDTO;
import model.impl.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LibraryManager {
    void addBook(long id, Integer amount, LocalDate addDate);

    void writeOff(long id, Integer amount, LocalDate saleDate);

    void initialize(LibraryManagerDTO libraryManagerDTO);

    Map<Long, Book> getBooks();

    List<Book> getBooksAsList();

    List<Book> getBooks(List<Long> booksIds);

    Optional<Book> getMaybeBook(Long bookId);

    boolean containsBook(long bookId);

    void importBook(Book importBook);
}
