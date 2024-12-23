package managers.impl;

import DTO.BookDTO;
import DTO.LibraryManagerDTO;
import managers.LibraryManager;
import model.impl.Book;

import java.time.LocalDate;
import java.util.*;

public class LibraryManagerImpl implements LibraryManager {
    private final Map<Long, Book> books;

    LibraryManagerImpl(LibraryManagerDTO dto) {
        books = new HashMap<>();
        for (BookDTO bookDTO : dto.books()) {
            Book book = new Book(bookDTO);
            books.put(bookDTO.id(), book);
        }
    }

    @Override
    public boolean writeOff(long id, Integer amount, LocalDate saleDate) {
        Optional<Book> book = getMaybeBook(id);
        if (book.isEmpty()) {
            return false;
        }
        book.get().setAmount(-amount);
        book.get().setLastSaleDate(saleDate);
        return true;
    }

    @Override
    public boolean addBook(long id, Integer amount, LocalDate addDate) {
        Optional<Book> book = getMaybeBook(id);
        if (book.isEmpty()) {
            return false;
        }
        book.get().setAmount(amount);
        book.get().setLastDeliveredDate(addDate);
        return true;
    }

    @Override
    public Map<Long, Book> getBooks() {
        return books;
    }

    @Override
    public List<Book> getBooksAsList() {
        return getBooks().values().stream().toList();
    }

    @Override
    public List<Book> getBooks(List<Long> booksIds) {
        List<Book> result = new ArrayList<>();
        for (Long bookId : booksIds) {
            if (books.get(bookId) != null) {
                result.add(books.get(bookId));
            }
        }
        return result;
    }

    @Override
    public Optional<Book> getMaybeBook(Long bookId) {
        return Optional.ofNullable(books.get(bookId));
    }

    @Override
    public boolean containsBook(long bookId) {
        return books.containsKey(bookId);
    }

    @Override
    public void importBook(Book importBook) {
        books.put(importBook.getId(), importBook);
    }
}
