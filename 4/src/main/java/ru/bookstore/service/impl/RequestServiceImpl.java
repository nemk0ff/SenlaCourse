package ru.bookstore.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bookstore.dao.RequestDao;
import ru.bookstore.exceptions.EntityNotFoundException;
import ru.bookstore.model.impl.Book;
import ru.bookstore.model.impl.Request;
import ru.bookstore.service.RequestService;
import ru.bookstore.sorting.RequestSort;

@Service
@Data
@Slf4j
public class RequestServiceImpl implements RequestService {
  private final RequestDao requestDao;

  @Transactional(readOnly = true)
  @Override
  public Request getRequest(Long requestId) {
    return requestDao.getRequestById(requestId).orElseThrow(()
        -> new EntityNotFoundException("Запрос [" + requestId + "] не найден"));
  }

  @Transactional
  @Override
  public Long addRequest(Book book, Integer amount) {
    return requestDao.addRequest(book, amount);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Request> getAllRequests() {
    return requestDao.getAllRequests(RequestSort.ID);
  }

  @Transactional(readOnly = true)
  @Override
  public LinkedHashMap<Book, Long> getRequestsByCount() {
    return requestDao.getRequests(RequestSort.COUNT);
  }

  @Transactional(readOnly = true)
  @Override
  public LinkedHashMap<Book, Long> getRequestsByPrice() {
    return requestDao.getRequests(RequestSort.PRICE);
  }

  @Transactional
  @Override
  public void importRequest(Request request) {
    log.info("Импорт запроса {}...", request.getId());
    Optional<Request> findRequest = requestDao.getRequestById(request.getId());
    if (findRequest.isPresent()) {
      throw new IllegalArgumentException("Ошибка при импорте: Запрос " + request.getId()
          + " уже есть в магазине");
    } else {
      requestDao.importRequest(request);
    }
  }

  @Transactional
  @Override
  public void closeRequests(Map<Long, Integer> books) {
    requestDao.closeRequests(books);
  }
}
