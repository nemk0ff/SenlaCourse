package ru.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.model.BookStatus;
import ru.model.impl.Book;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
  private Long id;
  private String name;
  private String author;
  private Integer publicationDate;
  private Integer amount;
  private Double price;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss dd-MM-yyyy")
  private LocalDateTime lastDeliveredDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss dd-MM-yyyy")
  private LocalDateTime lastSaleDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private BookStatus status;

  public BookDTO(Book book) {
    this.id = book.getId();
    this.name = book.getName();
    this.author = book.getAuthor();
    this.publicationDate = book.getPublicationDate();
    this.amount = book.getAmount();
    this.price = book.getPrice();
    this.lastDeliveredDate = book.getLastDeliveredDate();
    this.lastSaleDate = book.getLastSaleDate();
    this.status = book.getStatus();
  }
}