package Model;

import java.time.LocalDate;
import java.util.Objects;

public class Order {
    private OrderStatus status;
    private final Book book;
    private Double price;
    private LocalDate completeDate;
    private final String clientName;

    public Order(Book book, OrderStatus status, LocalDate completeDate, String clientName){
        this.book = book;
        this.status = status;
        this.price = book.getPrice();
        this.completeDate = completeDate;
        this.clientName = clientName;
    }

    public Order(Book book, String clientName){
        this.book = book;
        this.price = book.getPrice();
        this.clientName = clientName;
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

    public void setPrice(Double price) {
        this.price = price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Book getBook(){
        return book;
    }

    public String getClientName() { return this.clientName; }

    public String getInfoAbout(){
        return clientName + ",  " + price + ",  " + status + ",  " + (completeDate == null ? "null" : completeDate.toString());
    }

    public String getInfoAboutBook(){
        return book.getName() + ",  " + book.getAuthor() + ",  " + book.getPublicationDate();
    }

    Boolean isCompleted(){
        return (status == OrderStatus.COMPLETED);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(clientName, order.clientName) && Objects.equals(book, order.book);
    }
}
