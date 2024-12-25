package DTO;

import model.OrderStatus;
import model.impl.Order;

import java.time.LocalDate;
import java.util.Map;

public record OrderDTO(
        Long id,
        String clientName,
        OrderStatus status,
        Map<Long, Integer> books,
        Double price,
        LocalDate orderDate,
        LocalDate completeDate) {
    public OrderDTO(Order order) {
        this(
                order.getId(),
                order.getClientName(),
                order.getStatus(),
                order.getBooks(),
                order.getPrice(),
                order.getOrderDate(),
                order.getCompleteDate()
        );
    }
}
