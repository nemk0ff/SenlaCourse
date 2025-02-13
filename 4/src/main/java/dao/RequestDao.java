package dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.impl.Request;
import sorting.RequestSort;

/**
 * {@code RequestDao} - Интерфейс, определяющий поведение для (DAO) сущности {@link Request}.
 */
public interface RequestDao {
  List<Request> getAllRequests(RequestSort typeSort);

  LinkedHashMap<Long, Long> getRequests(RequestSort typeSort);

  Optional<Request> getRequestById(long requestId);

  Optional<Request> getRequestByBook(long bookId, int amount);

  long addRequest(long bookId, int amount) throws IllegalArgumentException;

  void importRequest(Request request) throws IllegalArgumentException;

  void closeRequest(long requestId) throws IllegalArgumentException;

  void closeRequests(Map<Long, Integer> book);
}
