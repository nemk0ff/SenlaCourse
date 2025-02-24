package model.impl;

import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.BookStatus;
import model.Item;

/**
 * {@code Book} - Класс, представляющий книгу в магазине.
 * Реализует интерфейс {@link Item}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "library")
public class Book implements Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "book_id")
  private Long id;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private String author;
  @Column(nullable = false)
  private Integer publicationDate;
  @Column(nullable = false)
  private Integer amount;
  @Column(nullable = false)
  private Double price;
  @Column
  private LocalDateTime lastDeliveredDate;
  @Column
  private LocalDateTime lastSaleDate;
  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private BookStatus status;

  /**
   * Устанавливает количество экземпляров книги, увеличивая текущее значение на заданное.
   * Обновляет статус книги в зависимости от текущего количества.
   */
  public void setAmount(Integer amount) {
    this.amount = amount;
    if (this.amount > 0) {
      status = BookStatus.AVAILABLE;
    } else {
      this.amount = 0;
      status = BookStatus.NOT_AVAILABLE;
    }
  }

  @Override
  public String getInfoAbout() {
    return "[" + id + "]   " + name + ",  " + author + ",  " + publicationDate
        + ",  " + price + ",  " + amount + ",  " + status.toString() + ",  "
        + (lastDeliveredDate == null ? "not been delivered yet" : lastDeliveredDate.toString())
        + ",  " + (lastSaleDate == null ? "not been sold yet" : lastSaleDate.toString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Book book = (Book) o;
    return Objects.equals(id, book.id);
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
    return id + "," + name + "," + author + "," + publicationDate + "," + amount + "," + price
        + "," + lastDeliveredDate + "," + lastSaleDate + "," + status;
  }

  /**
   * Преобразует строковое представление статуса книги в объект {@link BookStatus}.
   *
   * <p>Пытается найти соответствующее значение {@link BookStatus} по имени, игнорируя регистр.
   * Если соответствующее значение не найдено, статус определяется
   * на основе значения параметра {@code amount}:</p>
   */
  public static BookStatus getStatusFromString(String input, int amount) {
    for (BookStatus status : BookStatus.values()) {
      if (status.name().equalsIgnoreCase(input)) {
        return status;
      }
    }
    return amount > 0 ? BookStatus.AVAILABLE : BookStatus.NOT_AVAILABLE;
  }
}
