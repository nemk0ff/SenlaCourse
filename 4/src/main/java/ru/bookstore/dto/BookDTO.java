package ru.bookstore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bookstore.model.BookStatus;
import ru.bookstore.model.impl.Book;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
  @Positive(message = "id must be positive")
  private Long id;
  private String name;
  private String author;
  private Integer publicationDate;
  @Positive(message = "amount must be positive")
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