import java.time.LocalDate;
import java.util.List;

public interface LibraryManager {
    void addBook(Book book, Integer amount, LocalDate addDate);
    void writeOff(Book book, Integer amount, LocalDate saleDate);
    Boolean isAvailable(Book book);
    List<Book> getBooks();
}
