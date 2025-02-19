package model.impl;

import com.sun.istack.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
  @NotNull
  @Column(nullable = false)
  private String name;
  @NotNull
  @Column(nullable = false)
  private String author;
  @NotNull
  @Column(nullable = false)
  private Integer publicationDate;
  @NotNull
  @Column(nullable = false)
  private Integer amount;
  @NotNull
  @Column(nullable = false)
  private Double price;
  @NotNull
  @Column(nullable = false)
  private LocalDateTime lastDeliveredDate;
  @Column
  private LocalDateTime lastSaleDate;
  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
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
