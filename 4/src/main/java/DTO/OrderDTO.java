package DTO;

import model.impl.Order;
import model.OrderStatus;

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
    OrderDTO(Order order) {
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
