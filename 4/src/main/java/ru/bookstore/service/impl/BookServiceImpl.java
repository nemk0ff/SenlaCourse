package ru.bookstore.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bookstore.dao.BookDao;
import ru.bookstore.exceptions.EntityNotFoundException;
import ru.bookstore.model.impl.Book;
import ru.bookstore.service.BookService;
import ru.bookstore.sorting.BookSort;

@Service
@Data
@Slf4j
public class BookServiceImpl implements BookService {
  protected final BookDao bookDao;

  public BookServiceImpl(BookDao bookDao) {
    this.bookDao = bookDao;
  }

  @Transactional(readOnly = true)
  @Override
  public Book get(Long id) {
    return bookDao.getBookById(id).orElseThrow(()
        -> new EntityNotFoundException("Книга [" + id + "] не найдена"));
  }

  @Transactional
  @Override
  public void add(Long id, Integer amount, LocalDateTime addTime) {
    bookDao.add(id, amount, addTime);
  }

  @Transactional
  @Override
  public void writeOff(Long id, Integer amount, LocalDateTime addTime) {
    bookDao.writeOff(id, amount, addTime);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Book> getBooks(List<Long> bookIds) {
    return bookDao.getBooks(bookIds);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Book> getAllBooksById() {
    return bookDao.getAllBooks(BookSort.ID);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Book> getAllBooksByName() {
    return bookDao.getAllBooks(BookSort.NAME);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Book> getAllBooksByDate() {
    return bookDao.getAllBooks(BookSort.PUBLICATION_DATE);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Book> getAllBooksByPrice() {
    return bookDao.getAllBooks(BookSort.PRICE);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Book> getAllBooksByAvailable() {
    return bookDao.getAllBooks(BookSort.STATUS);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Book> getAllStaleBooksByDate() {
    return bookDao.getAllBooks(BookSort.STALE_BY_DATE);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Book> getAllStaleBooksByPrice() {
    return bookDao.getAllBooks(BookSort.STALE_BY_PRICE);
  }

  @Transactional
  @Override
  public void importBook(Book book) {
    bookDao.importBook(book);
  }
}
