package Controllers;

import Model.Book;

public interface BooksController {
    Book getBookFromConsole();

    void addBook();
    void writeOff();
    void showBookDetails();
    void getBooksByAlphabet();
    void getBooksByDate();
    void getBooksByPrice();
    void getBooksByAvailable();
    void getStaleBooksByDate();
    void getStaleBooksByPrice();
}
