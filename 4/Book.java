import java.util.Objects;

public class Book {
    private final String name;
    private final String author;
    private BookStatus status;
    private Integer amount;

    public Book(String name, String author, Integer amount) {
        this.name = name;
        this.author = author;
        this.amount = amount;

        if(this.amount > 0){
            this.status = BookStatus.Available;
        } else{
            this.amount = 0;
            this.status = BookStatus.NotAvailable;
        }
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

    void setAmount(Integer amount){
        this.amount += amount;
        if(amount > 0){
            status = BookStatus.Available;
        }
        else{
            this.amount = 0;
            status = BookStatus.NotAvailable;
        }
    }

    Integer getAmount(){
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(name, book.name) && Objects.equals(author, book.author);
    }
}
