package dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.impl.Book;
import model.impl.Request;
import org.hibernate.Session;
import sorting.RequestSort;

/**
 * {@code RequestDao} - Интерфейс, определяющий поведение для (DAO) сущности {@link Request}.
 */
public interface RequestDao extends GenericDao<Request> {
  List<Request> getAllRequests(Session session, RequestSort typeSort);

  LinkedHashMap<Book, Long> getRequests(Session session, RequestSort typeSort);

  Optional<Request> getRequestById(Session session, long requestId);

  long addRequest(Session session, Book book, int amount) throws IllegalArgumentException;

  void importRequest(Session session, Request request) throws IllegalArgumentException;

  void closeRequests(Session session, Map<Long, Integer> book);
}
