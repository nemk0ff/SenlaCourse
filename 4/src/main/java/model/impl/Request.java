package model.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import model.Item;
import model.RequestStatus;

/**
 * {@code Request} - Класс, представляющий запрос на книги магазина.
 * Реализует интерфейс {@link Item}.
 */
@Data
@AllArgsConstructor
public class Request implements Item {
  private static long counter = 0L;

  private final long id;
  private final long bookId;
  private final int amount;
  private RequestStatus status;

  @Override
  public long getId() {
    return id;
  }

  @Override
  public String getInfoAbout() {
    return "[" + id + "]   книга №" + bookId + ", количество: " + amount + ",  статус:" + status;
  }

  @Override
  public String toString() {
    return id + "," + bookId + "," + status.toString();
  }
}
