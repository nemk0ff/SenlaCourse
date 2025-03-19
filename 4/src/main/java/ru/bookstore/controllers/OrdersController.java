package ru.bookstore.controllers;

import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import ru.bookstore.dto.OrderDTO;
import ru.bookstore.model.OrderStatus;

public interface OrdersController {
  ResponseEntity<?> createOrder(OrderDTO orderDTO);

  ResponseEntity<?> cancelOrder(Long id);

  ResponseEntity<?> showOrderDetails(Long id);

  ResponseEntity<?> setOrderStatus(@Positive Long id, OrderStatus newStatus);

  ResponseEntity<?> getOrdersByDate();

  ResponseEntity<?> getOrdersByPrice();

  ResponseEntity<?> getOrdersByStatus();

  ResponseEntity<?> getCompletedOrdersByDate(LocalDateTime begin, LocalDateTime end);

  ResponseEntity<?> getCompletedOrdersByPrice(LocalDateTime begin, LocalDateTime end);

  ResponseEntity<?> getCountCompletedOrders(LocalDateTime begin, LocalDateTime end);

  ResponseEntity<?> getEarnedSum(LocalDateTime begin, LocalDateTime end);

  ResponseEntity<?> importAll();

  ResponseEntity<?> exportAll();

  ResponseEntity<?> importOrder(Long id);

  ResponseEntity<?> exportOrder(Long id);
}
