package Model;

import Model.Items.Impl.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LibraryManager {
    boolean addBook(long id, Integer amount, LocalDate addDate);

    boolean writeOff(long id, Integer amount, LocalDate saleDate);

    List<Book> getBooks();

    List<Book> getBooks(List<Long> booksIds);

    Optional<Book> getMaybeBook(Long bookId);

    boolean containsBook(long bookId);

    void importBook(Book importBook);
}
