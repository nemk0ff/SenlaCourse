package ru.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ru.model.impl.Book;
import ru.model.impl.Request;
import ru.sorting.RequestSort;

public interface RequestDao extends GenericDao<Request> {
  List<Request> getAllRequests(RequestSort typeSort);

  LinkedHashMap<Book, Long> getRequests(RequestSort typeSort);

  Optional<Request> getRequestById(long requestId);

  long addRequest(Book book, int amount) throws IllegalArgumentException;

  void importRequest(Request request) throws IllegalArgumentException;

  void closeRequests(Map<Long, Integer> book);
}
