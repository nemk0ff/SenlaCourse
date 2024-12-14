package Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LibraryManager {
    boolean addBook(long id, Integer amount, LocalDate addDate);

    boolean writeOff(long id, Integer amount, LocalDate saleDate);

    boolean isAvailable(Book book);

    List<Book> getBooks();

    List<Book> getBooks(List<Long> booksIds);

    Optional<Book> getMaybeBook(Long bookId);

    Book getBook(Long bookId);

    void importBook(Book importBook);
}
