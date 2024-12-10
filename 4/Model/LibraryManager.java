package Model;

import java.time.LocalDate;
import java.util.List;

public interface LibraryManager {
    boolean addBook(Book book, Integer amount, LocalDate addDate);

    boolean writeOff(Book book, Integer amount, LocalDate saleDate);

    boolean isAvailable(Book book);

    List<Book> getBooks();
}
