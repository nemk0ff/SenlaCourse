package model.impl;

import com.sun.istack.NotNull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.Item;
import model.RequestStatus;

/**
 * {@code Request} - Класс, представляющий запрос на книги магазина.
 * Реализует интерфейс {@link Item}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class Request implements Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "request_id")
  private Long id;
  @NotNull
  @ManyToOne
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;
  @NotNull
  @Column(nullable = false)
  private Integer amount;
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 10, nullable = false)
  private RequestStatus status;

  public Long getBookId() {
    return book.getId();
  }

  @Override
  public String getInfoAbout() {
    return "[" + id + "]   книга №" + book.getId() + ", количество: " + amount
        + ",  статус:" + status;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public String toString() {
    return id + "," + book.getId() + "," + amount + "," + status;
  }
}
