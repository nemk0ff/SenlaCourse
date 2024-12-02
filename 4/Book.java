import java.time.LocalDate;
import java.util.Objects;

public class Book {
    private final String name;
    private final String author;
    private Integer publicationDate = null;
    private LocalDate lastDeliveredDate = null;
    private final Integer price;
    private BookStatus status = BookStatus.NotAvailable;
    private Integer amount;

    // Конструктор для создания книги, которая лежит в магазине
    public Book(String name, String author, Integer amount, Integer price,
                Integer publicationDate, LocalDate lastDeliveredDate) {
        this.name = name;
        this.author = author;
        this.amount = amount;
        this.price = price;
        this.publicationDate = publicationDate;
        this.lastDeliveredDate = lastDeliveredDate;

        if(this.amount > 0){
            this.status = BookStatus.Available;
        } else{
            this.amount = 0;
        }
    }

    // Конструктор для книги, по которой создают заказ
    public Book(String name, String author, Integer price, Integer publicationDate) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.publicationDate = publicationDate;
    }

    public String getName() {
        return name;
    }

    public String getAuthor(){
        return author;
    }

    public BookStatus getStatus() {
        return status;
    }

    public LocalDate getLastDeliveredDate() {
        return lastDeliveredDate;
    }

    public void setAmount(Integer amount){
        this.amount += amount;
        if(this.amount > 0){
            status = BookStatus.Available;
        }
        else{
            this.amount = 0;
            status = BookStatus.NotAvailable;
        }
    }

    public Integer getPrice(){
        return price;
    }

    public Integer getPublicationDate(){
        return publicationDate;
    }

    public String getInfoAbout(){
        return name + ",  " + author + ",  " + publicationDate
                + ",  " + price + ",  " + amount + ",  " + status.toString()
                + ",  " + (lastDeliveredDate == null ? "null" : lastDeliveredDate.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(name, book.name) && Objects.equals(author, book.author)
                && Objects.equals(publicationDate, ((Book) o).publicationDate)
                && Objects.equals(price, ((Book) o).price);
    }
}
