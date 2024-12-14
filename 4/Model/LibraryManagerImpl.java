package Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryManagerImpl implements LibraryManager {
    private final List<Book> books;

    LibraryManagerImpl() {
        books = new ArrayList<>(List.of(
                new Book("Анна Каренина", "Л.Н.Толстой", 1, 1150.0, 1877,
                        LocalDate.of(2024, 4, 5), LocalDate.of(2024, 2, 23)),

                new Book("Капитанская дочка", "А.С.Пушкин", 1, 200.0, 1836,
                        LocalDate.of(2024, 4, 21), LocalDate.of(2024, 3, 19)),

                new Book("Дубровский", "А.С.Пушкин", 2, 450.0, 1833,
                        LocalDate.of(2024, 10, 20), LocalDate.of(2024, 5, 1)),

                new Book("Пиковая дама", "А.С.Пушкин", 1, 975.0, 1833,
                        LocalDate.of(2024, 4, 5), LocalDate.of(2024, 2, 23)),

                new Book("Обломов", "И.А.Гончаров", 1, 1000.0, 1859,
                        LocalDate.of(2024, 4, 21), LocalDate.of(2024, 3, 19)),

                new Book("Детство", "Л.Н.Толстой", 2, 1100.0, 1852,
                        LocalDate.of(2024, 10, 20), LocalDate.of(2024, 5, 1)),

                new Book("Муму", "И.С.Тургенев", 0, 475.0,
                        1852, LocalDate.of(2024, 1, 11), null),

                new Book("Отцы и дети", "И.С.Тургенев", 0, 925.0,
                        1861, LocalDate.of(2024, 1, 11), null),

                new Book("Мёртвые души", "Н.В.Гоголь", 0, 350.0,
                        1842, LocalDate.of(2024, 1, 11), null),

                new Book("Ревизор", "Н.В.Гоголь", 0, 875.0,
                        1835, LocalDate.of(2024, 1, 11), null)
        ));
    }

    @Override
    public boolean writeOff(Book book, Integer amount, LocalDate saleDate) {
        for (Book value : books) {
            if (value.equals(book)) {
                value.setAmount(-amount);
                book.setLastSaleDate(saleDate);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addBook(Book book, Integer amount, LocalDate addDate) {
        for (Book value : books) {
            if (value.equals(book)) {
                value.setAmount(amount);
                value.setLastDeliveredDate(addDate);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAvailable(Book book) {
        for (Book value : books) {
            if (value.equals(book) && value.getStatus() == BookStatus.AVAILABLE) {
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
