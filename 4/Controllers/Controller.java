package Controllers;

import View.Menu;

import java.util.Scanner;

public interface Controller {
    Scanner scanner = new Scanner(System.in);

    Action run();

    Action checkInput();

    default long getBookFromConsole(Menu menu, int index) {
        menu.showGetBookId(index);
        return getNumberFromConsole(menu);
    }

    default long getBookFromConsole(Menu menu) {
        menu.showGetId("Введите id книги: ");
        return getNumberFromConsole(menu);
    }

    default long getNumberFromConsole(Menu menu) {
        long answer;
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                answer = Long.parseLong(input);
                break;
            } catch (NumberFormatException e) {
                menu.showError("Неверный формат, попробуйте еще раз");
            }
        }
        return answer;
    }
}
