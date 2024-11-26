import java.util.ArrayList;
import java.util.List;

public class LibraryManagerImpl implements LibraryManager{
    private final List<Book> books;

    LibraryManagerImpl(){
        books = new ArrayList<>();
        books.add(new Book("Война и Мир", "Л.Н.Толстой", BookStatus.Available));
        books.add(new Book("Анна Каренина", "Л.Н.Толстой", BookStatus.Available));
        books.add(new Book("Капитанская дочка", "А.С.Пушкин", BookStatus.Available));
        books.add(new Book("Мёртвые души", "Н.В.Гоголь", BookStatus.NotAvailable));
        books.add(new Book("Ревизор", "Н.В.Гоголь", BookStatus.NotAvailable));
        books.add(new Book("Дубровский", "А.С.Пушкин", BookStatus.Available));
    }

    // Списать со склада
    @Override
    public void writeOff(Book book){
        for (Book value : books) {
            if (value.equals(book)) {
                value.setStatus(BookStatus.NotAvailable);
            }
        }
    }

    // Добавить книгу
    @Override
    public void addBook(Book book){
        book.setStatus(BookStatus.NotAvailable);
        for (Book value : books) {
            if (value.equals(book)) {
                value.setStatus(BookStatus.Available);
                return;
            }
        }
        book.setStatus(BookStatus.Available);
        books.add(book);
    }

    // Проверить, доступна ли книга
    public Boolean isAvailable(Book book){
        for (Book value : books) {
            if (value.equals(book) && value.getStatus() == BookStatus.Available){
                return true;
            }
        }
        return false;
    }

    public List<Book> getBooks() {
        return books;
    }
}
