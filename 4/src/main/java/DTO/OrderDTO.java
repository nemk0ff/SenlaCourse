package DTO;

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
}
