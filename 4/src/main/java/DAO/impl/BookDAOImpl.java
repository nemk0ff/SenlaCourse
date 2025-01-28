package DAO.impl;

import DAO.BookDAO;
import annotations.ConfigProperty;
import annotations.DIComponentDependency;
import config.ConfigurationManager;
import config.DatabaseConnection;
import lombok.Data;
import model.BookStatus;
import model.impl.Book;
import sorting.BookSort;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class BookDAOImpl implements BookDAO {
    @ConfigProperty(propertyName = "book.stale.months", type = int.class)
    private int staleBookMonths;
    @DIComponentDependency
    DatabaseConnection databaseConnection;

    public BookDAOImpl() {
        ConfigurationManager.configure(this);
    }

    @Override
    public boolean containsBook(long book_id) {
        return getBookById(book_id).isPresent();
    }

    @Override
    public void add(long book_id, int amount, LocalDateTime deliveredDate) throws IllegalArgumentException {
        Optional<Book> ourBook = getBookById(book_id);
        if (ourBook.isEmpty()) {
            throw new IllegalArgumentException("Такой книги нет в магазине");
        } else if (amount < 0) {
            throw new IllegalArgumentException("Количество книг для добавления должно быть положительным");
        }

        int newAmount = ourBook.get().getAmount() + amount;
        try {
            setAmount(book_id, newAmount, "lastDeliveredDate", deliveredDate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeOff(long book_id, int amount, LocalDateTime saleDate) throws IllegalArgumentException {
        Optional<Book> ourBook = getBookById(book_id);
        if (ourBook.isEmpty()) {
            throw new IllegalArgumentException("Такой книги нет в магазине");
        } else if (ourBook.get().getAmount() < amount) {
            throw new IllegalArgumentException("На складе нет столько книг, чтобы их списать");
        } else if (amount <= 0) {
            throw new IllegalArgumentException("Количество книг для списания должно быть положительным");
        }

        int newAmount = ourBook.get().getAmount() - amount;
        try {
            setAmount(book_id, newAmount, "lastSaleDate", saleDate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setAmount(long book_id, int amount, String dateType, LocalDateTime dateSet) throws SQLException {
        try (PreparedStatement preparedStatement = databaseConnection.connection().prepareStatement
                ("UPDATE library SET amount = ?, " + dateType + " = ? WHERE book_id = ?")) {

            preparedStatement.setInt(1, amount);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(dateSet));
            preparedStatement.setLong(3, book_id);

            if (preparedStatement.executeUpdate() == 0) {
                throw new SQLException("Ошибка бд при добавлении книги в базу данных: ни одна строка не изменена");
            }
            databaseConnection.connection().commit();
        } catch (SQLException e) {
            databaseConnection.connection().rollback();
            throw e;
        }
    }

    @Override
    public List<Book> getAllBooks(BookSort sortType) {
        List<Book> books = new ArrayList<>();

        String query = getQuery(sortType);

        try (PreparedStatement preparedStatement = databaseConnection.connection().prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                getBook(resultSet).ifPresent(books::add);
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }
        return books;
    }

    private String getQuery(BookSort sortType) {
        return switch (sortType) {
            case BookSort.ID -> "SELECT * FROM library ORDER BY book_id";
            case BookSort.NAME -> "SELECT * FROM library ORDER BY name";
            case BookSort.PUBLICATION_DATE -> "SELECT * FROM library ORDER BY publicationDate";
            case BookSort.PRICE -> "SELECT * FROM library ORDER BY price";
            case BookSort.STATUS -> "SELECT * FROM library ORDER BY status";
            case STALE_BY_DATE -> "SELECT * FROM library WHERE " +
                    "(lastSaleDate IS NULL AND DATEDIFF(CURDATE(), lastDeliveredDate) >= " + staleBookMonths + ") " +
                    "OR (lastSaleDate IS NOT NULL AND DATEDIFF(CURDATE(), lastSaleDate) >= " + staleBookMonths + ") " +
                    "ORDER BY lastDeliveredDate";
            case STALE_BY_PRICE -> "SELECT * FROM library WHERE " +
                    "(lastSaleDate IS NULL AND DATEDIFF(CURDATE(), lastDeliveredDate) >= " + staleBookMonths + ") " +
                    "OR (lastSaleDate IS NOT NULL AND DATEDIFF(CURDATE(), lastSaleDate) >= " + staleBookMonths + ") " +
                    "ORDER BY price";
        };
    }

    @Override
    public List<Book> getBooks(List<Long> bookIds) {
        List<Book> books = new ArrayList<>();

        StringBuilder partOfQuery = new StringBuilder();
        for (int i = 0; i < bookIds.size(); i++) {
            partOfQuery.append("?");
            if (i < bookIds.size() - 1) {
                partOfQuery.append(",");
            }
        }

        try (PreparedStatement statement = databaseConnection.connection().prepareStatement
                ("SELECT * FROM library WHERE book_id IN (" + partOfQuery + ")")) {
            for (int i = 0; i < bookIds.size(); i++) {
                statement.setLong(i + 1, bookIds.get(i));
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                getBook(resultSet).ifPresent(books::add);
            }
        } catch (SQLException e) {
            return new ArrayList<>();
        }
        return books;
    }

    @Override
    public void importBook(Book book) throws IllegalArgumentException {
        String query = "INSERT INTO library (book_id, name, author, publicationDate, " +
                "amount, price, lastDeliveredDate, lastSaleDate, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "AS new_values " +
                "ON DUPLICATE KEY UPDATE " +
                "name = new_values.name, " +
                "author = new_values.author, " +
                "publicationDate = new_values.publicationDate, " +
                "amount = new_values.amount, " +
                "price = new_values.price, " +
                "lastDeliveredDate = new_values.lastDeliveredDate, " +
                "lastSaleDate = new_values.lastSaleDate, " +
                "status = new_values.status";

        try (PreparedStatement preparedStatement = databaseConnection.connection().prepareStatement(query)) {

            preparedStatement.setLong(1, book.getId());
            preparedStatement.setString(2, book.getName());
            preparedStatement.setString(3, book.getAuthor());
            preparedStatement.setInt(4, book.getPublicationDate());
            preparedStatement.setInt(5, book.getAmount());
            preparedStatement.setDouble(6, book.getPrice());
            preparedStatement.setString(7, book.getLastDeliveredDate().toString());
            preparedStatement.setString(8, book.getLastSaleDate().toString());
            preparedStatement.setString(9, book.getStatus().toString());

            if (preparedStatement.executeUpdate() == 0) {
                throw new SQLException("Ошибка бд при изменении количества книг: ни одна книга не изменена");
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
    public Optional<Book> getBookById(long id) {
        try (Statement statement = databaseConnection.connection().createStatement()) {
            ResultSet results = statement.executeQuery("SELECT * FROM library WHERE book_id = " + id);
            results.next();
            return getBook(results);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    private Optional<Book> getBook(ResultSet results) {
        try {
            return Optional.of(new Book(results.getLong(1),
                    results.getString(2),
                    results.getString(3),
                    results.getInt(4),
                    results.getInt(5),
                    results.getDouble(6),
                    results.getObject(7, LocalDateTime.class),
                    results.getObject(8, LocalDateTime.class),
                    getStatusFromString(results.getString(9), results.getInt(5))));
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    private BookStatus getStatusFromString(String input, int amount) {
        for (BookStatus status : BookStatus.values()) {
            if (status.name().equalsIgnoreCase(input)) {
                return status;
            }
        }
        // Если в бд будет статус неверного формата, то статус будет определен по полю amount
        return amount > 0 ? BookStatus.AVAILABLE : BookStatus.NOT_AVAILABLE;
    }
}
