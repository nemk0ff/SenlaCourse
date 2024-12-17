package Controllers;

import View.Menu;

import java.util.*;

public interface Controller {
    Scanner scanner = new Scanner(System.in);

    Action run();

    Action checkInput();

    static long getNumberFromConsole(Menu menu) {
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
