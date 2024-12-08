package Controllers;

import Model.Book;
import View.Menu;

import java.util.Scanner;

public interface Controller {
    Scanner scanner = new Scanner(System.in);
    Action run();
    Action checkInput();

    default Book getBookFromConsole(Menu menu){
        // TODO: добавить проверки на ввод некорректных данных, обернуть в Optional
        menu.showGetName();
        String name = scanner.nextLine();

        menu.showGetAuthor();
        String author = scanner.nextLine();

        menu.showGetPublicationDate();
        Integer publicationDate = scanner.nextInt();
        scanner.nextLine();

        menu.showGetPrice();
        Integer price = scanner.nextInt();
        scanner.nextLine();

        return new Book(name, author, price, publicationDate);
    }
}
