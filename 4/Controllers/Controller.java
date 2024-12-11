package Controllers;

import Model.Book;
import View.Menu;

import java.util.Scanner;

public interface Controller {
    Scanner scanner = new Scanner(System.in);

    Action run();

    Action checkInput();

    default Book getBookFromConsole(Menu menu) {
        menu.showGetName();
        String name = scanner.nextLine().trim();

        menu.showGetAuthor();
        String author = scanner.nextLine().trim();

        return new Book(name, author, 0);
    }
}
