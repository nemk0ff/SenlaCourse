package View;

import Model.Book;
import java.util.List;

public interface BooksMenu {
    void showBooks(List<Book> books);
    void showBook(Book book);
    void showGetName();
    void showGetAuthor();
    void showGetPrice();
    void showGetPublicationDate();
    void showGetAmountAdd();
    void showGetAmountWriteOff();
}
