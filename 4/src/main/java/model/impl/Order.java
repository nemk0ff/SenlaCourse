package model.impl;

import DTO.OrderDTO;
import model.Item;
import model.OrderStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Data
public class Order implements Item {
    static Long counter = 0L;

    private final Long id;
    private OrderStatus status;
    private Map<Long, Integer> books;
    private Double price;
    private LocalDate orderDate;
    private LocalDate completeDate;
    private String clientName;

    // Для создания заказа магазина
    public Order(Map<Long, Integer> books, double price, OrderStatus status, LocalDate orderDate, String clientName) {
        counter++;
        this.id = counter;

        this.books = books;
        this.status = status;
        this.orderDate = orderDate;
        this.clientName = clientName;
        this.price = price;
        this.completeDate = null;
    }

    // Для создания импортируемого заказа
    public Order(long id, String clientName, double price, OrderStatus status, LocalDate orderDate,
                 LocalDate completeDate, Map<Long, Integer> books) {
        this.id = id;
        this.clientName = clientName;
        this.price = price;
        this.status = status;
        this.orderDate = orderDate;
        this.completeDate = completeDate;
        this.books = books;
    }

    public Order(OrderDTO dto) {
        this.id = dto.id();
        this.clientName = dto.clientName();
        this.price = dto.price();
        this.status = dto.status();
        this.orderDate = dto.orderDate();
        this.completeDate = dto.completeDate();
        this.books = dto.books();
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

    public void copyOf(Order other) {
        this.clientName = other.clientName;
        this.price = other.price;
        this.status = other.status;
        this.orderDate = other.orderDate;
        this.completeDate = other.completeDate;
        this.books = other.books;
    }
}
