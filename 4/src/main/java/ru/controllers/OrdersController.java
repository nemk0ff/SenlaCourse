package ru.controllers;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.ResponseEntity;
import ru.dto.OrderDTO;

public interface OrdersController {
  ResponseEntity<?> createOrder(OrderDTO orderDTO);

  void cancelOrder(Long id);

  OrderDTO showOrderDetails(Long id);

  void setOrderStatus(OrderDTO orderDTO);

  List<OrderDTO> getOrdersByDate();

  List<OrderDTO> getOrdersByPrice();

  List<OrderDTO> getOrdersByStatus();

  List<OrderDTO> getCompletedOrdersByDate(LocalDateTime begin, LocalDateTime end);

  List<OrderDTO> getCompletedOrdersByPrice(LocalDateTime begin, LocalDateTime end);

  Long getCountCompletedOrders(LocalDateTime begin, LocalDateTime end);

  Double getEarnedSum(LocalDateTime begin, LocalDateTime end);

  void importAll();

  void exportAll();

  void importOrder(Long id);

  void exportOrder(Long id);
}
