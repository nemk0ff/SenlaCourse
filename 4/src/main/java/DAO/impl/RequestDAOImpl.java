package DAO.impl;

import DAO.RequestDAO;
import annotations.DIComponentDependency;
import config.DatabaseConnection;
import model.RequestStatus;
import model.impl.Book;
import model.impl.Request;
import sorting.RequestSort;

import java.sql.*;
import java.util.*;

public class RequestDAOImpl implements RequestDAO {
    @DIComponentDependency
    DatabaseConnection databaseConnection;

    public RequestDAOImpl() {
    }

    @Override
    public List<Request> getAllRequests(RequestSort typeSort) {
        List<Request> requests = new ArrayList<>();

        String query = getQuery(typeSort);

        try (PreparedStatement preparedStatement = databaseConnection.connection().prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                getRequest(resultSet).ifPresent(requests::add);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
        return requests;
    }

    @Override
    public LinkedHashMap<Long, Long> getRequests(RequestSort typeSort) {
        LinkedHashMap<Long, Long> requests = new LinkedHashMap<>();

        String query = getQuery(typeSort);

        try (PreparedStatement preparedStatement = databaseConnection.connection().prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                requests.put(resultSet.getLong(1), resultSet.getLong(2));
            }
        } catch (SQLException e) {
            return new LinkedHashMap<>();
        }
        return requests;
    }

    private String getQuery(RequestSort sortType) {
        return switch (sortType) {
            case RequestSort.ID -> "SELECT * FROM requests ORDER BY request_id";
            case RequestSort.COUNT -> "SELECT book_id, COUNT(*) as count FROM requests WHERE status = 'OPEN' " +
                    "GROUP BY book_id ORDER BY count";
            case RequestSort.PRICE -> "SELECT l.book_id, COUNT(*) FROM requests r JOIN library l " +
                    "on r.book_id = l.book_id WHERE r.status = 'OPEN' GROUP BY l.book_id ORDER BY l.price";
        };
    }

    @Override
    public Optional<Request> getRequestById(long request_id) {
        try (Statement statement = databaseConnection.connection().createStatement()) {
            ResultSet results = statement.executeQuery("SELECT * FROM requests WHERE request_id = " + request_id);
            results.next();
            return getRequest(results);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Request> getRequestByBook(long book_id, int amount) {
        try (Statement statement = databaseConnection.connection().createStatement()) {
            ResultSet results = statement.executeQuery
                    ("SELECT * FROM requests WHERE book_id = " + book_id + " AND amount = " + amount + " LIMIT 1");
            results.next();
            return getRequest(results);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void addRequest(long book_id, int amount) {
        String insertOrderQuery = "INSERT INTO requests (book_id, amount, status) VALUES (?, ?, ?)";

        try (PreparedStatement orderStatement = databaseConnection.connection().prepareStatement(insertOrderQuery)) {
            orderStatement.setLong(1, book_id);
            orderStatement.setInt(2, amount);
            orderStatement.setString(3, "OPEN");

            if (orderStatement.executeUpdate() == 0) {
                throw new RuntimeException("Ошибка бд при создании запроса: ни одна строка не изменена");
            }
            databaseConnection.connection().commit();
        } catch (SQLException e) {
            try {
                databaseConnection.connection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void importRequest(Request request) {
        String query = "INSERT INTO requests (request_id, book_id, amount, status) VALUES (?, ?, ?) ";

        try (PreparedStatement preparedStatement = databaseConnection.connection().prepareStatement(query)) {
            preparedStatement.setLong(1, request.getBookId());
            preparedStatement.setInt(2, request.getAmount());
            preparedStatement.setString(3, request.getStatus().toString());

            if (preparedStatement.executeUpdate() == 0) {
                throw new RuntimeException("Ошибка бд при импорте запроса: ни одна строка не изменена");
            }
            databaseConnection.connection().commit();
        } catch (SQLException e) {
            try {
                databaseConnection.connection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeRequest(long request_id) {
        String updateQuery = "UPDATE requests SET status = ? WHERE request_id = ?";
        try (PreparedStatement statement = databaseConnection.connection().prepareStatement(updateQuery)) {
            statement.setString(1, "CLOSED");
            statement.setLong(2, request_id);

            if (statement.executeUpdate() == 0) {
                throw new IllegalArgumentException("Не удалось закрыть запрос №" + request_id + ": запрос не найден");
            }
            databaseConnection.connection().commit();
        } catch (SQLException e) {
            try {
                databaseConnection.connection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeRequests(Map<Long, Integer> books) {
        for (Map.Entry<Long, Integer> book : books.entrySet()) {
            Optional<Request> optionalRequest = getRequestByBook(book.getKey(), book.getValue());
            optionalRequest.ifPresent(request -> closeRequest(request.getId()));
        }
    }

    private Optional<Request> getRequest(ResultSet resultOrder) {
        try{
            return Optional.of(new Request(resultOrder.getLong(1),
                    resultOrder.getLong(2),
                    resultOrder.getInt(3),
                    getStatusFromString(resultOrder.getString(4))));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private RequestStatus getStatusFromString(String input) {
        for (RequestStatus status : RequestStatus.values()) {
            if (status.name().equalsIgnoreCase(input)) {
                return status;
            }
        }
        return null;
    }
}
