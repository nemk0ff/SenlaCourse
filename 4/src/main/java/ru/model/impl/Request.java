package ru.model.impl;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.model.Item;
import ru.model.RequestStatus;

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
  @ManyToOne
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;
  @Column(nullable = false)
  private Integer amount;
  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 10, nullable = false)
  private RequestStatus status;

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
