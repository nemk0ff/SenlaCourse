package ru.bookstore.controllers.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bookstore.constants.FileConstants;
import ru.bookstore.controllers.OrdersController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.bookstore.controllers.impl.importexport.ExportController;
import ru.bookstore.controllers.impl.importexport.ImportController;
import ru.bookstore.dto.OrderDTO;
import ru.bookstore.manager.MainManager;
import ru.bookstore.model.OrderStatus;
import ru.bookstore.model.impl.Order;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrdersControllerImpl implements OrdersController {
  private final MainManager mainManager;

  @PostMapping("createOrder")
  @Override
  public ResponseEntity<?> createOrder(@RequestBody @Valid OrderDTO orderDTO) {
    return ResponseEntity.ok(new OrderDTO(mainManager.createOrder(orderDTO.getBooks(),
        orderDTO.getClientName(), LocalDateTime.now())));
  }

  @PostMapping("cancelOrder/{id}")
  @Override
  public ResponseEntity<?> cancelOrder(@PathVariable("id") @Positive Long id) {
    mainManager.cancelOrder(id);
    return ResponseEntity.ok("Заказ отменён");
  }

  @GetMapping("showOrder/{id}")
  @Override
  public ResponseEntity<?> showOrderDetails(@PathVariable("id") Long id) {
    return ResponseEntity.ok(new OrderDTO(mainManager.getOrder(id)));
  }

  @PostMapping("setOrderStatus")
  @Override
  public ResponseEntity<?> setOrderStatus(@RequestParam("id") @Positive Long id,
                                          @RequestParam("amount") OrderStatus newStatus) {
    mainManager.setOrderStatus(id, newStatus);
    return ResponseEntity.ok("Статус заказа изменён на " + newStatus);
  }

  @GetMapping("getOrders/byDate")
  @Override
  public ResponseEntity<?> getOrdersByDate() {
    return ResponseEntity.ok(mainManager.getAllOrdersByDate()
        .stream()
        .map(OrderDTO::new)
        .toList());
  }

  @GetMapping("getOrders/byPrice")
  @Override
  public ResponseEntity<?> getOrdersByPrice() {
    return ResponseEntity.ok(mainManager.getAllOrdersByPrice()
        .stream()
        .map(OrderDTO::new)
        .toList());
  }

  @GetMapping("getOrders/byStatus")
  @Override
  public ResponseEntity<?> getOrdersByStatus() {
    return ResponseEntity.ok(mainManager.getAllOrdersByStatus()
        .stream()
        .map(OrderDTO::new)
        .toList());
  }

  @GetMapping("getCompletedOrders/byDate")
  @Override
  public ResponseEntity<?> getCompletedOrdersByDate(
      @RequestParam(value = "begin", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime begin,
      @RequestParam(value = "end", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime end) {
    return ResponseEntity.ok(mainManager.getCompletedOrdersByDate(begin, end)
        .stream()
        .map(OrderDTO::new)
        .toList());
  }

  @GetMapping("getCompletedOrders/byPrice")
  @Override
  public ResponseEntity<?> getCompletedOrdersByPrice(
      @RequestParam(value = "begin", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime begin,
      @RequestParam(value = "end", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime end) {
    return ResponseEntity.ok(mainManager.getCompletedOrdersByPrice(begin, end)
        .stream()
        .map(OrderDTO::new)
        .toList());
  }

  @GetMapping("getCountCompletedOrders")
  @Override
  public ResponseEntity<?> getCountCompletedOrders(
      @RequestParam(value = "begin", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime begin,
      @RequestParam(value = "end", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime end) {
    return ResponseEntity.ok(mainManager.getCountCompletedOrders(begin, end));
  }

  @GetMapping("getEarnedSum")
  @Override
  public ResponseEntity<?> getEarnedSum(
      @RequestParam(value = "begin", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime begin,
      @RequestParam(value = "end", required = false)
      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime end) {
    return ResponseEntity.ok(mainManager.getEarnedSum(begin, end));
  }

  @PutMapping("importAll")
  @Override
  public ResponseEntity<?> importAll() {
    List<Order> importedOrders = ImportController.importAllItemsFromFile(
        FileConstants.IMPORT_ORDER_PATH, ImportController::orderParser);
    importedOrders.forEach(mainManager::importOrder);
    return ResponseEntity.ok(importedOrders.stream().map(OrderDTO::new).toList());
  }

  @PutMapping("exportAll")
  @Override
  public ResponseEntity<?> exportAll() {
    List<Order> exportOrders = mainManager.getAllOrders();
    ExportController.exportAll(exportOrders,
        FileConstants.EXPORT_ORDER_PATH, FileConstants.ORDER_HEADER);
    return ResponseEntity.ok(exportOrders.stream().map(OrderDTO::new).toList());
  }

  @PutMapping("importOrder/{id}")
  @Override
  public ResponseEntity<?> importOrder(@PathVariable("id") Long id) {
    Order findOrder = ImportController.findItemInFile(id, FileConstants.IMPORT_ORDER_PATH,
        ImportController::orderParser);
    mainManager.importItem(findOrder);
    return ResponseEntity.ok(new OrderDTO(findOrder));
  }

  @PutMapping("exportOrder/{id}")
  @Override
  public ResponseEntity<?> exportOrder(@PathVariable("id") Long id) {
    Order exportOrder = mainManager.getOrder(id);
    ExportController.exportItemToFile(exportOrder,
        FileConstants.EXPORT_ORDER_PATH, FileConstants.ORDER_HEADER);
    return ResponseEntity.ok(new OrderDTO(exportOrder));
  }
}