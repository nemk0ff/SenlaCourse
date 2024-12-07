package View;

import Model.Book;
import java.util.List;

public class BooksMenuImpl implements Menu, BooksMenu{

    @Override
    public void showMenu() {
        System.out.println("##############################");
        System.out.println("########  МЕНЮ КНИГ  #########");
        System.out.println("##############################");
        System.out.println("    Выберите действие:");
        System.out.println("1. Добавить книгу на склад");
        System.out.println("2. Списать книгу со склада");
        System.out.println("3. Посмотреть описание книги");
        System.out.println("4. Вывести список книг библиотеки (сортировка по алфавиту)");
        System.out.println("5. Вывести список книг библиотеки (сортировка по дате издания)");
        System.out.println("6. Вывести список книг библиотеки (сортировка по цене)");
        System.out.println("7. Вывести список книг библиотеки (сортировка по наличию на складе)");
        System.out.println("8. Вывести список залежавшихся книг (сортировка по дате поступления)");
        System.out.println("9. Вывести список залежавшихся книг (сортировка по цене)");
        System.out.println("10. Вернуться в главное меню");
        System.out.println("11. Выйти из программы");
    }

    @Override
    public void showInputError() { System.out.println("Вы ввели неизвестную команду"); }

    @Override
    public void showBooks(List<Book> books) { books.forEach(book -> System.out.println(book.getInfoAbout())); }

    @Override
    public void showBook(Book book) { System.out.println(book.getInfoAbout()); }

    @Override
    public void showGetName() { System.out.print("Введите название книги: "); }

    @Override
    public void showGetAuthor() { System.out.print("Введите автора книги (В формате \"И.О.Фамилия\"): "); }

    @Override
    public void showGetPrice() { System.out.print("Введите цену книги: "); }

    @Override
    public void showGetPublicationDate() { System.out.print("Введите дату публикации книги: "); }

    @Override
    public void showGetAmountAdd() { System.out.print("Сколько книг добавить? "); }

    @Override
    public void showGetAmountWriteOff() { System.out.print("Сколько книг списать? "); }
}
