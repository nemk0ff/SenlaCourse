package Model;

import java.time.LocalDate;
import java.util.Objects;

public class Book {
    private final String name;
    private final String author;
    private final Integer publicationDate;
    private LocalDate lastDeliveredDate;
    private LocalDate lastSaleDate;
    private final Double price;
    private BookStatus status = BookStatus.NOT_AVAILABLE;
    private Integer amount;

    // Конструктор для создания книги, которая лежит в магазине
    public Book(String name, String author, Integer amount, Double price,
                Integer publicationDate, LocalDate lastDeliveredDate, LocalDate lastSaleDate) {
        this.name = name;
        this.author = author;
        this.amount = amount;
        this.price = price;
        this.publicationDate = publicationDate;
        this.lastDeliveredDate = lastDeliveredDate;
        this.lastSaleDate = lastSaleDate;

        if (this.amount > 0) {
            this.status = BookStatus.AVAILABLE;
        } else {
            this.amount = 0;
        }
    }

    // Конструктор для книги, по которой создают заказ
    public Book(String name, String author, Double price, Integer publicationDate) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.publicationDate = publicationDate;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public BookStatus getStatus() {
        return status;
    }

    public LocalDate getLastSaleDate() {
        return lastSaleDate;
    }

    public void setLastSaleDate(LocalDate lastSaleDate) {
        this.lastSaleDate = lastSaleDate;
    }

    public void setLastDeliveredDate(LocalDate lastSaleDate) {
        this.lastSaleDate = lastSaleDate;
    }

    public LocalDate getLastDeliveredDate() {
        return lastDeliveredDate;
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

    public Integer getAmount() {
        return amount;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getPublicationDate() {
        return publicationDate;
    }

    public String getInfoAbout() {
        return name + ",  " + author + ",  " + publicationDate
                + ",  " + price + ",  " + amount + ",  " + status.toString()
                + ",  " + (lastDeliveredDate == null ? "null" : lastDeliveredDate.toString())
                + ",  " + (lastSaleDate == null ? "null" : lastSaleDate.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(name, book.name) && Objects.equals(author, book.author)
                && Objects.equals(publicationDate, book.publicationDate)
                && Objects.equals(price, book.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, author, publicationDate, price);
    }
}
