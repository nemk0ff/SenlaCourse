package View;

import Model.Book;
import java.util.List;

public interface BooksMenu extends Menu{
    void showBooks(List<Book> books);
    void showBook(Book book);
    void showGetAmountAdd();
    void showGetAmountWriteOff();
}
