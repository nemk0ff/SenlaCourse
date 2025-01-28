package model.impl;

import model.Item;
import model.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Data
public class Order implements Item {
    private Long id = 0L;
    private OrderStatus status;
    private Map<Long, Integer> books;
    private Double price;
    private LocalDateTime orderDate;
    private LocalDateTime completeDate;
    private String clientName;

    // Для создания заказа магазина
    public Order(Map<Long, Integer> books, double price, OrderStatus status, LocalDateTime orderDate, String clientName) {
        this.books = books;
        this.status = status;
        this.orderDate = orderDate;
        this.clientName = clientName;
        this.price = price;
        this.completeDate = null;
    }

    // Для создания импортируемого заказа
    public Order(long id, OrderStatus status, double price, LocalDateTime orderDate,
                 LocalDateTime completeDate, String clientName, Map<Long, Integer> books) {
        this.id = id;
        this.clientName = clientName;
        this.price = price;
        this.status = status;
        this.orderDate = orderDate;
        this.completeDate = completeDate;
        this.books = books;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getInfoAbout() {
        return "[" + id + "]  " + clientName + ",  " + price + ",  " + status + ",  " + orderDate.toString()
                + ",  " + (completeDate == null ? "not been completed yet" : completeDate.toString());
    }

    public Boolean isCompleted() {
        return (status == OrderStatus.COMPLETED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        StringBuilder resultString = new StringBuilder();
        resultString.append(id).append(",")
                .append(clientName).append(",")
                .append(price).append(",")
                .append(status).append(",")
                .append(orderDate).append(",")
                .append(completeDate);

        for (Map.Entry<Long, Integer> book : books.entrySet()) {
            resultString.append(",").append(book.getKey()).append(",").append(book.getValue());
        }
        return resultString.toString();
    }
}
