package view.impl;

import view.BooksMenu;

/**
 * {@code BooksMenuImpl} - Реализация интерфейса {@link BooksMenu}, предоставляющая методы для
 * отображения меню управления книгами пользователю.
 */
public class BooksMenuImpl implements BooksMenu {
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
    System.out.println("10. Импортировать книгу");
    System.out.println("11. Экспортировать книгу");
    System.out.println("12. Импортировать все книги");
    System.out.println("13. Экспортировать все книги");
    System.out.println("14. Вернуться в главное меню");
    System.out.println("15. Выйти из программы");
  }
}
