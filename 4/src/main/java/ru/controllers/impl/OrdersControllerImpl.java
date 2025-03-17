package ru.controllers.impl;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.controllers.OrdersController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.dto.OrderDTO;
import ru.manager.MainManager;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrdersControllerImpl implements OrdersController {
  private final MainManager mainManager;

  @PostMapping("createOrder")
  @Override
  public ResponseEntity<?> createOrder(@RequestBody @Valid OrderDTO orderDTO) {
    OrderDTO createdOrder = new OrderDTO(mainManager.createOrder(orderDTO.getBooks(),
        orderDTO.getClientName(), LocalDateTime.now()));
    return ResponseEntity.ok(createdOrder);
  }

  @PostMapping("cancelOrder/{id}")
  @Override
  public void cancelOrder(@PathVariable("id") Long id) {
    mainManager.cancelOrder(id);
  }

  @GetMapping("showOrder/{id}")
  @Override
  public OrderDTO showOrderDetails(@PathVariable("id") Long id) {
    return new OrderDTO(mainManager.getOrder(id));
  }

  @PostMapping("setOrderStatus")
  @Override
  public void setOrderStatus(@RequestBody OrderDTO orderDTO) {
    mainManager.setOrderStatus(orderDTO.getId(), orderDTO.getStatus());
  }

  @GetMapping("getOrders/byDate")
  @Override
  public List<OrderDTO> getOrdersByDate() {
    return mainManager.getAllOrdersByDate()
        .stream()
        .map(OrderDTO::new)
        .toList();
  }

  @GetMapping("getOrders/byPrice")
  @Override
  public List<OrderDTO> getOrdersByPrice() {
    return mainManager.getAllOrdersByPrice()
        .stream()
        .map(OrderDTO::new)
        .toList();
  }

  @GetMapping("getOrders/byStatus")
  @Override
  public List<OrderDTO> getOrdersByStatus() {
    return mainManager.getAllOrdersByStatus()
        .stream()
        .map(OrderDTO::new)
        .toList();
  }

  @GetMapping("getCompletedOrders/byDate")
  @Override
  public List<OrderDTO> getCompletedOrdersByDate(
      @RequestParam(value = "begin", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime begin,
      @RequestParam(value = "end", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime end) {
    return mainManager.getCompletedOrdersByDate(begin, end)
        .stream()
        .map(OrderDTO::new)
        .toList();
  }

  @GetMapping("getCompletedOrders/byPrice")
  @Override
  public List<OrderDTO> getCompletedOrdersByPrice(
      @RequestParam(value = "begin", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime begin,
      @RequestParam(value = "end", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime end) {
    return mainManager.getCompletedOrdersByPrice(begin, end)
        .stream()
        .map(OrderDTO::new)
        .toList();
  }

  @GetMapping("getCountCompletedOrders")
  @Override
  public Long getCountCompletedOrders(
      @RequestParam(value = "begin", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime begin,
      @RequestParam(value = "end", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime end) {
    return mainManager.getCountCompletedOrders(begin, end);
  }

  @GetMapping("getEarnedSum")
  @Override
  public Double getEarnedSum(
      @RequestParam(value = "begin", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime begin,
      @RequestParam(value = "end", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime end) {
    return mainManager.getEarnedSum(begin, end);
  }

  @GetMapping("importAll")
  @Override
  public void importAll() {
  }

  @GetMapping("exportAll")
  @Override
  public void exportAll() {
  }

  @GetMapping("importOrder/{id}")
  @Override
  public void importOrder(@PathVariable("id") Long id) {
  }

  @GetMapping("exportOrder/{id}")
  @Override
  public void exportOrder(@PathVariable("id") Long id) {
  }
}