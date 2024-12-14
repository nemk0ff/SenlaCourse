package Model;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class Order {
    static Long counter = 0L;

    private final Long id;
    private OrderStatus status;
    private final Map<Long, Integer> books;
    private Double price;
    private final LocalDate orderDate;
    private LocalDate completeDate;
    private final String clientName;

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

    public long getId() {
        return id;
    }

    public LocalDate getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(LocalDate completeDate) {
        this.completeDate = completeDate;
    }

    public Double getPrice() {
        return price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Map<Long, Integer> getBooks() {
        return books;
    }

    public String getInfoAbout() {
        return "[" + id + "]  " + clientName + ",  " + price + ",  " + status + ",  " + orderDate.toString()
                + ",  " + (completeDate == null ? "not been completed yet" : completeDate.toString());
    }

    Boolean isCompleted() {
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
}
