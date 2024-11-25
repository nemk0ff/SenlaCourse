import java.util.Objects;

public class Book {
    private final String name;
    private final String author;
    private BookStatus status;

    public Book(String name, String author, BookStatus status) {
        this.name = name;
        this.author = author;
        this.status = status;
    }

    public Book(String name, String author) {
        this.name = name;
        this.author = author;
        this.status = BookStatus.NotAvailable;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(name, book.name) && Objects.equals(author, book.author);
    }
}
