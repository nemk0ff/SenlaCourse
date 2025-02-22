package model.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.Item;
import model.OrderStatus;

/**
 * {@code Order} - Класс, представляющий заказ в магазине.  Реализует интерфейс {@link Item}.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "orders")
public class Order implements Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Long id;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private OrderStatus status;
  @Column(nullable = false)
  private Double price;
  @Column(nullable = false)
  private LocalDateTime orderDate;
  @Column
  private LocalDateTime completeDate;
  @Column(nullable = false)
  private String clientName;

  @ElementCollection
  @CollectionTable(name = "ordered_books",
      joinColumns = @JoinColumn(name = "order_id"))
  @MapKeyColumn(name = "book_id")
  @Column(name = "amount")
  private Map<Long, Integer> books;

  /**
   * Конструктор для создания нового заказа в магазине.
   * Поле {@code completeDate} устанавливается в {@code null}.
   */
  public Order(Map<Long, Integer> books, double price, OrderStatus status,
               LocalDateTime orderDate, String clientName) {
    this.books = books;
    this.status = status;
    this.orderDate = orderDate;
    this.clientName = clientName;
    this.price = price;
  }

  /**
   * Конструктор для создания заказа при импорте данных из файла.
   */
  public Order(long id, OrderStatus status, double price, LocalDateTime orderDate,
               LocalDateTime completeDate, String clientName, Map<Long, Integer> books) {
    this.id = id;
    this.clientName = clientName;
    this.price = price;
    this.status = status;
    this.orderDate = orderDate;
    this.completeDate = completeDate;
    this.books = books;
  }

  @Override
  public String getInfoAbout() {
    return "[" + id + "]  " + clientName + ",  " + price + ",  "
        + status + ",  " + orderDate.toString() + ",  "
        + (completeDate == null ? "not been completed yet" : completeDate.toString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Order order = (Order) o;
    return Objects.equals(id, order.id);
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    StringBuilder resultString = new StringBuilder();
    resultString.append(id).append(",")
        .append(clientName).append(",")
        .append(price).append(",")
        .append(status).append(",")
        .append(orderDate).append(",")
        .append(completeDate);

    for (Map.Entry<Long, Integer> book : books.entrySet()) {
      resultString.append(",").append(book.getKey()).append(",").append(book.getValue());
    }
    return resultString.toString();
  }
}
