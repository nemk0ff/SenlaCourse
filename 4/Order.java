import java.util.Objects;

public class Order {
    private OrderStatus status;
    private final Book book;

    Order(Book book, OrderStatus status){
        this.book = book;
        this.status = status;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return status == order.status && Objects.equals(book, order.book);
    }
}
