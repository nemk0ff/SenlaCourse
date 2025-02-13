package model.impl;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import model.BookStatus;
import model.Item;

/**
 * {@code Book} - Класс, представляющий книгу в магазине.
 * Реализует интерфейс {@link Item}.
 */
@Data
@AllArgsConstructor
public class Book implements Item {
  private final Long id;
  private String name;
  private String author;
  private Integer publicationDate;
  private Integer amount;
  private Double price;
  private LocalDateTime lastDeliveredDate;
  private LocalDateTime lastSaleDate;
  private BookStatus status;

  /**
   * Устанавливает количество экземпляров книги, увеличивая текущее значение на заданное.
   * Обновляет статус книги в зависимости от текущего количества.
   */
  public void setAmount(Integer amount) {
    this.amount += amount;
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
  public long getId() {
    return id;
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
