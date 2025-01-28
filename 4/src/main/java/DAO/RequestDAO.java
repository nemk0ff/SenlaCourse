package DAO;

import model.impl.Request;
import sorting.RequestSort;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RequestDAO {
    List<Request> getAllRequests(RequestSort typeSort);

    LinkedHashMap<Long, Long> getRequests(RequestSort typeSort);

    Optional<Request> getRequestById(long request_id);

    Optional<Request> getRequestByBook(long book_id, int amount);

    void addRequest(long book_id, int amount) throws IllegalArgumentException;

    void importRequest(Request request) throws IllegalArgumentException;

    void closeRequest(long request_id) throws IllegalArgumentException;

    void closeRequests(Map<Long, Integer> book);
}
