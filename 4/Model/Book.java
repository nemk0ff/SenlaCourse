package Model;

import java.time.LocalDate;
import java.util.Objects;

public class Book {
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

    public String getName() {
        return name;
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

    public void setLastDeliveredDate(LocalDate lastDeliveredDate) {
        this.lastDeliveredDate = lastDeliveredDate;
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
        return "[" + id + "]   " + name + ",  " + author + ",  " + publicationDate
                + ",  " + price + ",  " + amount + ",  " + status.toString()
                + ",  " + (lastDeliveredDate == null ? "not been delivered yet" : lastDeliveredDate.toString())
                + ",  " + (lastSaleDate == null ? "not been sold yet" : lastSaleDate.toString());
    }

    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }

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
