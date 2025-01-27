package DAO;

import model.impl.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookDAO {
    void add(long book_id, int amount, LocalDate deliveredDate) throws IllegalArgumentException;

    void writeOff(long book_id, int amount, LocalDate saleDate) throws IllegalArgumentException;

    List<Book> getAllBooks();

    List<Book> getBooks(List<Long> bookIds);

    Optional<Book> getBookById(long book_id);

    boolean containsBook(long bookId) throws IllegalArgumentException;

    void importBook(Book book) throws IllegalArgumentException;
}
