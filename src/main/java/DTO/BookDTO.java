package DTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import model.BookStatus;
import model.impl.Book;
import lombok.Data;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    private String name;
    private String author;
    private Double price;
    private Integer publicationDate;
    private Integer amount;
    private LocalDate lastDeliveredDate;
    private LocalDate lastSaleDate;
    private BookStatus status;

    BookDTO(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        this.author = book.getAuthor();
        this.price = book.getPrice();
        this.publicationDate = book.getPublicationDate();
        this.amount = book.getAmount();
        this.lastDeliveredDate = book.getLastDeliveredDate();
        this.lastSaleDate = book.getLastSaleDate();
        this.status = book.getStatus();
    }
}
