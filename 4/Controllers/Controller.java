package Controllers;

import Model.Book;
import View.Menu;

import java.util.Scanner;

public interface Controller {
    Scanner scanner = new Scanner(System.in);
    Action run();
    Action checkInput();

    default Book getBookFromConsole(Menu menu){
        menu.showGetName();
        String name = scanner.nextLine();

        menu.showGetAuthor();
        String author = scanner.nextLine();

        int publicationDate;
        while (true) {
            menu.showGetPublicationDate();
            String input = scanner.nextLine();
            try {
                publicationDate = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                menu.showError("Неверный формат даты публикации, попробуйте еще раз");
            }
        }

        double price;
        while (true) {
            menu.showGetPrice();
            String input = scanner.nextLine();
            try {
                price = Double.parseDouble(input);
                break;
            } catch (NumberFormatException e) {
                menu.showError("Неверный формат цены, попробуйте еще раз");
            }
        }

        return new Book(name, author, price, publicationDate);
    }
}
