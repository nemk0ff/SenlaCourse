package Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
    private OrderStatus status;
    private final List<Book> books;
    private Double price;
    private LocalDate orderDate;
    private LocalDate completeDate;
    private final String clientName;

    public Order(List<Book> books, OrderStatus status, LocalDate orderDate, String clientName) {
        this.books = books;
        this.status = status;
        this.orderDate = orderDate;
        this.clientName = clientName;
        this.price = getPrice(books);
        this.completeDate = null;
    }

    public Order(List<Book> books, String clientName) {
        this.books = books;
        this.price = getPrice(books);
        this.clientName = clientName;
    }

    public Double getPrice(List<Book> books) {
        return books.stream()
                .mapToDouble(Book::getPrice)
                .sum();
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

    public List<Book> getBooks() {
        return books;
    }

    public String getClientName() {
        return this.clientName;
    }

    public String getInfoAbout() {
        return clientName + ",  " + price + ",  " + status + ",  " +
                (completeDate == null ? "Not completed yet" : completeDate.toString());
    }

    public List<String> getInfoAboutBooks() {
        List<String> result = new ArrayList<>();
        for (Book book : books) {
            result.add(book.getName() + ",  " + book.getAuthor()
                    + ",  " + book.getPublicationDate() + ",  " + book.getPrice());
        }
        return result;
    }

    Boolean isCompleted() {
        return (status == OrderStatus.COMPLETED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(clientName, order.clientName) && Objects.equals(books, order.books);
    }
}
