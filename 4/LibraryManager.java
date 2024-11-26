import java.util.List;

public interface LibraryManager {
    void addBook(Book book, Integer amount);
    void writeOff(Book book, Integer amount);
    Boolean isAvailable(Book book);
    List<Book> getBooks();
}
