package managers;

import model.impl.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LibraryManager {
    void add(long id, int amount, LocalDate addDate) throws IllegalArgumentException;

    void writeOff(long id, int amount, LocalDate saleDate) throws IllegalArgumentException;

    List<Book> getAllBooks();

    List<Book> getBooks(List<Long> booksIds);

    Optional<Book> getBook(Long bookId);

    boolean containsBook(long bookId);

    void importBook(Book importBook) throws IllegalArgumentException;
}
