package managers;

import model.impl.Book;
import model.impl.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LibraryManager {
    boolean addBook(long id, Integer amount, LocalDate addDate);

    boolean writeOff(long id, Integer amount, LocalDate saleDate);

    Map<Long, Book> getBooks();

    List<Book> getBooksAsList();

    List<Book> getBooks(List<Long> booksIds);

    Optional<Book> getMaybeBook(Long bookId);

    boolean containsBook(long bookId);

    void importBook(Book importBook);
}
