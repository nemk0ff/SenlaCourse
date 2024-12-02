import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryManagerImpl implements LibraryManager{
    private final List<Book> books;
    
    LibraryManagerImpl(){
        books = new ArrayList<>();
        books.add(new Book("Война и Мир", "Л.Н.Толстой", 1, 100,
                1869, LocalDate.of(2024, 2, 12)));
        books.add(new Book("Анна Каренина", "Л.Н.Толстой", 1, 150,
                1877, LocalDate.of(2024, 4, 5)));
        books.add(new Book("Капитанская дочка", "А.С.Пушкин", 1, 200,
                1836, LocalDate.of(2024, 4, 21)));
        books.add(new Book("Мёртвые души", "Н.В.Гоголь", 0, 350,
                1842, null));
        books.add(new Book("Ревизор", "Н.В.Гоголь", 0, 200,
                1835, null));
        books.add(new Book("Дубровский", "А.С.Пушкин", 2, 450,
                1833, LocalDate.of(2024, 10, 20)));
    }

    // Списать со склада
    @Override
    public void writeOff(Book book, Integer amount){
        for (Book value : books) {
            if (value.equals(book)) {
                value.setAmount(-amount);
                return;
            }
        }
    }

    // Добавить книгу
    @Override
    public void addBook(Book book, Integer amount){
        for (Book value : books) {
            if (value.equals(book)) {
                value.setAmount(amount);
                return;
            }
        }
        books.add(book);
    }

    // Проверить, доступна ли книга
    @Override
    public Boolean isAvailable(Book book){
        for (Book value : books) {
            if (value.equals(book) && value.getStatus() == BookStatus.Available){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Book> getBooks() {
        return books;
    }
}
