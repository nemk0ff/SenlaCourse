package DTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import model.impl.Order;
import model.OrderStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class OrderDTO {
    private final Long id;
    private String clientName;
    private OrderStatus status;
    private Map<Long, Integer> books;
    private Double price;
    private LocalDate orderDate;
    private LocalDate completeDate;

    OrderDTO(Order order) {
        this.id = order.getId();
        this.clientName = order.getClientName();
        this.status = order.getStatus();
        this.books = order.getBooks();
        this.price = order.getPrice();
        this.orderDate = order.getOrderDate();
        this.completeDate = order.getCompleteDate();
    }
}
