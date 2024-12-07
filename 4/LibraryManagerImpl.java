import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryManagerImpl implements LibraryManager{
    private final List<Book> books;
    
    LibraryManagerImpl(){
        books = new ArrayList<>();
        books.add(new Book("Война и Мир", "Л.Н.Толстой", 1, 100,
                1869, LocalDate.of(2024, 2, 12), LocalDate.of(2024, 1, 11)));
        books.add(new Book("Анна Каренина", "Л.Н.Толстой", 1, 150,
                1877, LocalDate.of(2024, 4, 5), LocalDate.of(2024, 2, 23)));
        books.add(new Book("Капитанская дочка", "А.С.Пушкин", 1, 200,
                1836, LocalDate.of(2024, 4, 21), LocalDate.of(2024, 3, 19)));
        books.add(new Book("Мёртвые души", "Н.В.Гоголь", 0, 350,
                1842, LocalDate.of(2024, 1, 11), null));
        books.add(new Book("Ревизор", "Н.В.Гоголь", 2, 200,
                1835, LocalDate.of(2024, 1, 11), null));
        books.add(new Book("Дубровский", "А.С.Пушкин", 2, 450,
                1833, LocalDate.of(2024, 10, 20), LocalDate.of(2024, 5, 1)));
    }

    // Списать со склада
    @Override
    public void writeOff(Book book, Integer amount, LocalDate saleDate){
        for (Book value : books) {
            if (value.equals(book)) {
                value.setAmount(-amount);
                book.setLastSaleDate(saleDate);
                return;
            }
        }
    }

    // Добавить книгу
    @Override
    public void addBook(Book book, Integer amount, LocalDate addDate){
        for (Book value : books) {
            if (value.equals(book)) {
                value.setAmount(amount);
                value.setLastDeliveredDate(addDate);
                return;
            }
        }
        books.add(book);
    }

    // Проверить, доступна ли книга
    @Override
    public Boolean isAvailable(Book book){
        for (Book value : books) {
            if (value.equals(book) && value.getStatus() == BookStatus.AVAILABLE){
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
