package DAO.impl;

import DAO.OrderDAO;
import annotations.DIComponentDependency;
import config.DatabaseConnection;
import model.OrderStatus;
import model.impl.Order;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class OrderDAOImpl implements OrderDAO {
    @DIComponentDependency
    DatabaseConnection databaseConnection;

    public OrderDAOImpl() {
    }

    @Override
    public void setOrderStatus(long orderId, String status) {
        Savepoint save;
        try {
            save = databaseConnection.connection().setSavepoint();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String updateQuery = "UPDATE orders SET status = ?, completeDate = ? WHERE order_id = ?";
        try (PreparedStatement statement = databaseConnection.connection().prepareStatement(updateQuery)) {
            statement.setString(1, status);
            statement.setString(2, LocalDate.now().toString());
            statement.setLong(3, orderId);

            if (statement.executeUpdate() == 0) {
                throw new RuntimeException("Ошибка бд при изменении статуса заказа: ни одна строка не изменена");
            }
            databaseConnection.connection().commit();
        } catch (SQLException e) {
            try {
                databaseConnection.connection().rollback(save);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addOrder(Order order) {
        Savepoint save;
        try {
            save = databaseConnection.connection().setSavepoint();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String insertOrderQuery = "INSERT INTO orders (order_id, status, price, orderDate, completeDate, clientName) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String insertOrderedBooksQuery = "INSERT INTO ordered_books (order_id, book_id, amount) VALUES (?, ?, ?)";

        try (PreparedStatement orderStatement = databaseConnection.connection().prepareStatement(insertOrderQuery);
             PreparedStatement orderedBooksStatement = databaseConnection.connection()
                     .prepareStatement(insertOrderedBooksQuery)) {
            long newId = getNewOrderId();
            orderStatement.setLong(1, newId);
            orderStatement.setString(2, order.getStatus().toString());
            orderStatement.setDouble(3, order.getPrice());
            orderStatement.setObject(4, order.getOrderDate());
            orderStatement.setObject(5, order.getCompleteDate());
            orderStatement.setString(6, order.getClientName());

            if (orderStatement.executeUpdate() == 0) {
                throw new RuntimeException("Ошибка бд при добавлении заказа: ни одна строка не изменена");
            }

            for (Map.Entry<Long, Integer> entry : order.getBooks().entrySet()) {
                orderedBooksStatement.setLong(1, newId);
                orderedBooksStatement.setLong(2, entry.getKey());
                orderedBooksStatement.setInt(3, entry.getValue());
                orderedBooksStatement.addBatch();
            }
            int[] batchResults = orderedBooksStatement.executeBatch();
            for (int rowsAffected : batchResults) {
                if (rowsAffected == 0) {
                    throw new RuntimeException("Ошибка бд при обновлении списка заказанных книг: ни одна строка не изменена");
                }
            }
            databaseConnection.connection().commit();
        } catch (SQLException e) {
            try {
                databaseConnection.connection().rollback(save);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> allOrders = new ArrayList<>();

        try (Statement statement = databaseConnection.connection().createStatement();
             ResultSet resultSetOrders = statement.executeQuery("SELECT order_id FROM orders")) {
            while (resultSetOrders.next()) {
                long orderId = resultSetOrders.getLong("order_id");
                getOrderById(orderId).ifPresent(allOrders::add);
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return allOrders;
    }

    @Override
    public Optional<Order> getOrderById(long order_id) {
        try (Statement OrdersStatement = databaseConnection.connection().createStatement();
             Statement OrderedBooksStatement = databaseConnection.connection().createStatement()) {
            ResultSet resultOrder = OrdersStatement.executeQuery("SELECT * FROM orders WHERE order_id = " + order_id);
            resultOrder.next();
            ResultSet orderedBooks = OrderedBooksStatement.executeQuery
                    ("SELECT * FROM ordered_books WHERE order_id = " + order_id);
            return getOrder(resultOrder, orderedBooks);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Order> getOrder(ResultSet resultOrder, ResultSet orderedBooks) {
        Map<Long, Integer> books = new HashMap<>();
        try {
            while (orderedBooks.next()) {
                books.put(orderedBooks.getLong(2), orderedBooks.getInt(3));
            }
            return Optional.of(new Order(resultOrder.getLong(1),
                    getStatusFromString(resultOrder.getString(2)),
                    resultOrder.getDouble(3),
                    resultOrder.getObject(4, LocalDate.class),
                    resultOrder.getObject(5, LocalDate.class),
                    resultOrder.getString(6),
                    books));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private OrderStatus getStatusFromString(String input) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.name().equalsIgnoreCase(input)) {
                return status;
            }
        }
        return null;
    }

    private long getNewOrderId() throws SQLException {
        try (Statement statement = databaseConnection.connection().createStatement()) {
            ResultSet resultOrder = statement.executeQuery("SELECT MAX(order_id) FROM orders");
            resultOrder.next();
            return resultOrder.getLong(1) + 1;
        }
    }
}
