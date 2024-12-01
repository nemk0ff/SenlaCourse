import java.time.LocalDate;
import java.util.Objects;

public class Book {
    private final String name;
    private final String author;
    private Integer publicationDate;
    private Integer price;
    private BookStatus status;
    private Integer amount;

    public Book(String name, String author, Integer amount, Integer price, Integer publicationDate) {
        this.name = name;
        this.author = author;
        this.amount = amount;
        this.price = price;
        this.publicationDate = publicationDate;

        if(this.amount > 0){
            this.status = BookStatus.Available;
        } else{
            this.amount = 0;
            this.status = BookStatus.NotAvailable;
        }
    }

    public Book(String name, String author, Integer price, Integer publicationDate, BookStatus status) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.publicationDate = publicationDate;
        this.status = status;
    }

    public Book(String name, String author) {
        this.name = name;
        this.author = author;
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

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public void setAmount(Integer amount){
        this.amount += amount;
        if(amount > 0){
            status = BookStatus.Available;
        }
        else{
            this.amount = 0;
            status = BookStatus.NotAvailable;
        }
    }

    public Integer getAmount(){
        return amount;
    }

    public Integer getPrice(){
        return price;
    }

    public Integer getPublicationDate(){
        return publicationDate;
    }

    public String getInfoAbout(){
        return name + ",  " + author + ",  " + publicationDate
                + ",  " + price + ",  " + amount + ",  " + status.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(name, book.name) && Objects.equals(author, book.author);
    }
}
