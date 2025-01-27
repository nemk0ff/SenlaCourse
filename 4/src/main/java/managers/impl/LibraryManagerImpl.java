package managers.impl;

import DAO.impl.BookDAOImpl;
import annotations.DIComponentDependency;
import managers.LibraryManager;
import model.impl.Book;

import java.time.LocalDate;
import java.util.*;

public class LibraryManagerImpl implements LibraryManager {
    @DIComponentDependency
    BookDAOImpl bookDAO;

    public LibraryManagerImpl() {
    }

    @Override
    public void writeOff(long id, int amount, LocalDate saleDate) throws IllegalArgumentException {
        bookDAO.writeOff(id, amount, saleDate);
    }

    @Override
    public void add(long id, int amount, LocalDate deliveredDate) throws IllegalArgumentException {
        bookDAO.add(id, amount, deliveredDate);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    @Override
    public List<Book> getBooks(List<Long> booksIds) {
        return bookDAO.getBooks(booksIds);
    }

    @Override
    public Optional<Book> getBook(Long bookId) {
        return bookDAO.getBookById(bookId);
    }

    @Override
    public boolean containsBook(long book_id) {
        return bookDAO.containsBook(book_id);
    }

    @Override
    public void importBook(Book importBook) throws IllegalArgumentException {
        bookDAO.importBook(importBook);
    }
}
