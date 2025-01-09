package managers.impl;

import DTO.BookDTO;
import DTO.LibraryManagerDTO;
import managers.LibraryManager;
import model.impl.Book;

import java.time.LocalDate;
import java.util.*;

public class LibraryManagerImpl implements LibraryManager {
    private final Map<Long, Book> books;

    public LibraryManagerImpl() {
        this.books = new HashMap<>();
    }

    public void initialize(LibraryManagerDTO libraryManagerDTO) {
        for (BookDTO bookDTO : libraryManagerDTO.books()) {
            this.books.put(bookDTO.id(), new Book(bookDTO));
        }
    }

    @Override
    public void writeOff(long id, Integer amount, LocalDate saleDate) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("Число книг для списания должно быть положительным");
        }

        Optional<Book> book = getMaybeBook(id);
        if (book.isEmpty()) {
            throw new IllegalArgumentException("Книга для списания не найдена");
        }
        if (book.get().getAmount() < amount) {
            throw new IllegalArgumentException("Книг на складе меньше, чем вы хотите списать");
        }

        book.get().setAmount(-amount);
        book.get().setLastSaleDate(saleDate);
    }

    @Override
    public void addBook(long id, Integer amount, LocalDate addDate) throws IllegalArgumentException {
        Optional<Book> book = getMaybeBook(id);
        if (book.isEmpty()) {
            throw new IllegalArgumentException("Книга не найдена");
        }
        book.get().setAmount(amount);
        book.get().setLastDeliveredDate(addDate);
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
