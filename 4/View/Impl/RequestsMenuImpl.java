package View.Impl;

import Model.Book;
import View.RequestsMenu;

import java.util.LinkedHashMap;

public class RequestsMenuImpl implements RequestsMenu {

    @Override
    public void showMenu() {
        System.out.println("##############################");
        System.out.println("########  МЕНЮ ЗАПРОСОВ  #####");
        System.out.println("##############################");
        System.out.println("    Выберите действие:");
        System.out.println("1. Оставить запрос на книгу");
        System.out.println("2. Вывести список запросов на книги (сортировка по количеству запросов)");
        System.out.println("3. Вывести список запросов на книги (сортировка по цене)");
        System.out.println("4. Вернуться в главное меню");
        System.out.println("5. Выйти из программы");
    }

    @Override
    public void showRequests(LinkedHashMap<Book, Long> requests) {
        System.out.println("======= ОТКРЫТЫЕ ЗАПРОСЫ НА КНИГИ =======");
        requests.forEach((key, value) -> {
            System.out.println(key.getInfoAbout());
            System.out.println("Количество запросов: " + value);
            System.out.println();
        });
        System.out.println("=========================================");
    }
}
