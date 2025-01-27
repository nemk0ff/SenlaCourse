package model.impl;

import lombok.Data;
import model.BookStatus;
import model.Item;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class Book implements Item {
    private final Long id;
    private String name;
    private String author;
    private Integer publicationDate;
    private Integer amount;
    private Double price;
    private LocalDate lastDeliveredDate;
    private LocalDate lastSaleDate;
    private BookStatus status;

    public Book(Long id, String name, String author, Integer publicationDate, Integer amount, Double price,
                LocalDate lastDeliveredDate, LocalDate lastSaleDate, BookStatus status) {
        this.id = id;
        this.author = author;
        this.name = name;
        this.publicationDate = publicationDate;
        this.amount = amount;
        this.price = price;
        this.lastDeliveredDate = lastDeliveredDate;
        this.lastSaleDate = lastSaleDate;
        this.status = status;
    }

    public Book(Long id, String name, String author, Integer publicationDate, Integer amount, Double price,
                LocalDate lastDeliveredDate, LocalDate lastSaleDate) {
        this.id = id;
        this.author = author;
        this.name = name;
        this.publicationDate = publicationDate;
        this.amount = amount;
        this.price = price;
        this.lastDeliveredDate = lastDeliveredDate;
        this.lastSaleDate = lastSaleDate;
        this.status = amount > 0 ? BookStatus.AVAILABLE : BookStatus.NOT_AVAILABLE;
    }

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
                + ",  " + price + ",  " + amount + ",  " + status.toString()
                + ",  " + (lastDeliveredDate == null ? "not been delivered yet" : lastDeliveredDate.toString())
                + ",  " + (lastSaleDate == null ? "not been sold yet" : lastSaleDate.toString());
    }

    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void copyOf(Book book) {
        this.name = book.name;
        this.author = book.author;
        this.amount = book.amount;
        this.price = book.price;
        this.publicationDate = book.publicationDate;
        this.lastDeliveredDate = book.lastDeliveredDate;
        this.lastSaleDate = book.lastSaleDate;
        this.status = amount > 0 ? BookStatus.AVAILABLE : BookStatus.NOT_AVAILABLE;
    }

    @Override
    public String toString() {
        return id + "," + name + "," + author + "," + publicationDate + "," + amount + "," + price
                + "," + lastDeliveredDate + "," + lastSaleDate;
    }
}
