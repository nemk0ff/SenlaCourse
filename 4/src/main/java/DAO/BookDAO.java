package DAO;

import model.impl.Book;
import sorting.BookSort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookDAO {
    void add(long book_id, int amount, LocalDateTime deliveredDate) throws IllegalArgumentException;

    void writeOff(long book_id, int amount, LocalDateTime saleDate) throws IllegalArgumentException;

    List<Book> getAllBooks(BookSort sortType);

    List<Book> getBooks(List<Long> bookIds);

    Optional<Book> getBookById(long book_id);

    boolean containsBook(long bookId) throws IllegalArgumentException;

    void importBook(Book book) throws IllegalArgumentException;
}
