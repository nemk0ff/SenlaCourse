package dao.impl;

import dao.RequestDao;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import model.RequestStatus;
import model.impl.Book;
import model.impl.Request;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;
import sorting.RequestSort;

/**
 * {@code RequestDaoImpl} - Реализация интерфейса {@link RequestDao}, предоставляющая методы для
 * взаимодействия с базой данных для управления информацией о запросах.
 */
@Slf4j
public class RequestDaoImpl extends HibernateAbstractDao<Request> implements RequestDao {

  public RequestDaoImpl() {
    super(Request.class);
    log.debug("RequestDaoImpl инициализирован");
  }

  @Override
  public List<Request> getAllRequests(Session session, RequestSort typeSort) {
    log.info("Получаем все запросы, отсортированные по {}...", typeSort);
    try {
      List<Request> requests = session
          .createQuery("FROM Request ORDER BY id", Request.class)
          .getResultList();
      log.info("Успешно получено {} запросов", requests.size());
      return requests;
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при получении всех запросов: " + e.getMessage(), e);
    }
  }

  @Override
  public LinkedHashMap<Book, Long> getRequests(Session session, RequestSort typeSort) {
    log.debug("Получаем запросы, отсортированные по {}...", typeSort);

    if (typeSort == RequestSort.COUNT) {
      return getRequestsSortedByCount(session);
    } else if (typeSort == RequestSort.PRICE) {
      return getRequestsSortedByPrice(session);
    } else {
      throw new IllegalArgumentException("Неизвестный тип сортировки: " + typeSort);
    }
  }

  private LinkedHashMap<Book, Long> getRequestsSortedByCount(Session session) {
    String hql = "SELECT r.book, COUNT(r) FROM Request r "
        + "WHERE r.status = 'OPEN' GROUP BY r.book ORDER BY COUNT(r)";

    List<Object[]> resultList = session.createQuery(hql, Object[].class)
        .getResultList();
    return getBookLongLinkedHashMap(resultList);
  }

  private LinkedHashMap<Book, Long> getRequestsSortedByPrice(Session session) {
    Map<Book, Double> bookPrices = getBookPrices(session);

    String hql = "SELECT r.book, COUNT(r) FROM Request r WHERE r.status = 'OPEN' GROUP BY r.book";
    List<Object[]> resultList = session.createQuery(hql, Object[].class)
        .getResultList();

    List<Object[]> sortedResultList = resultList.stream()
        .sorted(Comparator.comparingDouble(
            result -> bookPrices.getOrDefault((Book) result[0], 0.0)))
        .toList();

    return getBookLongLinkedHashMap(sortedResultList);
  }

  private LinkedHashMap<Book, Long> getBookLongLinkedHashMap(List<Object[]> resultList) {
    LinkedHashMap<Book, Long> requests = new LinkedHashMap<>();

    for (Object[] result : resultList) {
      Book book = (Book) result[0];
      Long count = ((Number) result[1]).longValue();
      requests.put(book, count);
    }

    return requests;
  }

  private Map<Book, Double> getBookPrices(Session session) {
    String hql = "SELECT b.id, b.price FROM Book b";
    List<Object[]> resultList = session.createQuery(hql, Object[].class)
        .getResultList();

    return resultList.stream()
        .collect(Collectors.toMap(
            result -> session.find(Book.class, result[0]),
            result -> (Double) result[1]
        ));
  }

  @Override
  public Optional<Request> getRequestById(Session session, long requestId) {
    return find(session, requestId);
  }

  @Override
  public long addRequest(Session session, Book book, int amount) {
    log.debug("Добавляем запрос для bookId={} и amount={}", book.getId(), amount);
    try {
      Request request = new Request();
      request.setBook(book);
      request.setAmount(amount);
      request.setStatus(RequestStatus.OPEN);

      session.persist(request);
      return request.getId();
    } catch (Exception e) {
      throw new RuntimeException("Не удалось добавить запрос для bookId=" + book.getId()
          + " и amount=" + amount + " :" + e.getMessage(), e);
    }
  }

  @Override
  public void importRequest(Session session, Request request) {
    log.debug("Импортируем запрос: {}...", request);
    try {
      update(session, request);
      log.debug("Запрос успешно импортирован: {}", request);
    } catch (Exception e) {
      throw new RuntimeException("Не удалось импортировать запрос: " + request.getInfoAbout(), e);
    }
  }

  @Override
  public void closeRequests(Session session, Map<Long, Integer> books) {
    log.info("Закрываем запросы для книг: {}...", books);
    try {
      for (Map.Entry<Long, Integer> bookEntry : books.entrySet()) {
        Long bookId = bookEntry.getKey();
        Integer amount = bookEntry.getValue();
        Book book = session.find(Book.class, bookId);
        if (book == null) {
          log.warn("Книга [{}] не найдена. Пропускаем закрытие запроса.", bookId);
          continue;
        }
        Request request = session.createQuery("FROM Request WHERE book = :book " +
                "AND amount = :amount", Request.class)
            .setParameter("book", book)
            .setParameter("amount", amount)
            .setMaxResults(1)
            .uniqueResult();
        if (request != null) {
          request.setStatus(RequestStatus.CLOSED);
          session.persist(request);
          log.debug("Закрываем запрос [{}] для bookId={} и amount={}",
              request.getId(), bookId, amount);
        }
      }
      log.info("Запросы для книг {} успешно закрыты.", books);
    } catch (Exception e) {
      throw new RuntimeException("Не удалось закрыть запросы для книг "
          + books + ": " + e.getMessage(), e);
    }
  }
}
