package model.impl;

import DTO.BookDTO;
import lombok.Data;
import model.BookStatus;
import model.Item;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class Book implements Item {
    static Long counter = 0L;

    private final Long id;
    private String name;
    private String author;
    private Integer publicationDate;
    private LocalDate lastDeliveredDate;
    private LocalDate lastSaleDate;
    private Double price;
    private BookStatus status = BookStatus.NOT_AVAILABLE;
    private Integer amount;

    // Конструктор для создания книги, которая лежит в магазине
    public Book(String name, String author, Integer amount, Double price,
                Integer publicationDate, LocalDate lastDeliveredDate, LocalDate lastSaleDate) {
        counter++;
        id = counter;

        this.name = name;
        this.author = author;
        this.amount = amount;
        this.price = price;
        this.publicationDate = publicationDate;
        this.lastDeliveredDate = lastDeliveredDate;
        this.lastSaleDate = lastSaleDate;

        this.status = amount > 0 ? BookStatus.AVAILABLE : BookStatus.NOT_AVAILABLE;
    }

    // Конструктор для книг, которые импортируются
    public Book(long id, String name, String author, int amount, double price, int publicationDate,
                LocalDate lastDeliveredDate, LocalDate lastSaleDate) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.amount = amount;
        this.price = price;
        this.publicationDate = publicationDate;
        this.lastDeliveredDate = lastDeliveredDate;
        this.lastSaleDate = lastSaleDate;
        this.status = amount > 0 ? BookStatus.AVAILABLE : BookStatus.NOT_AVAILABLE;
    }

    public Book(BookDTO dto) {
        this.id = dto.id();
        this.name = dto.name();
        this.author = dto.author();
        this.amount = dto.amount();
        this.price = dto.price();
        this.publicationDate = dto.publicationDate();
        this.lastDeliveredDate = dto.lastDeliveredDate();
        this.lastSaleDate = dto.lastSaleDate();
        this.status = dto.status();
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
