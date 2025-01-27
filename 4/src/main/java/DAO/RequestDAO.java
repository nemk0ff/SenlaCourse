package DAO;

import model.impl.Request;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RequestDAO {
    List<Request> getRequests();

    Optional<Request> getRequestById(long request_id);

    Optional<Request> getRequestByBook(long book_id, int amount);

    void addRequest(long book_id, int amount) throws IllegalArgumentException;

    void importRequest(Request request) throws IllegalArgumentException;

    void closeRequest(long request_id) throws IllegalArgumentException;
}
