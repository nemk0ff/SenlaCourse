package Controllers;

import Model.MainManager;
import Model.MainManagerImpl;
import View.MainMenu;

import java.util.Scanner;

public class MainController implements Controller{
    private final MainMenu mainMenu;
    private final Scanner scanner;
    private final BooksControllerImpl booksController;

    public MainController() {
        MainManager mainManager = new MainManagerImpl();
        this.mainMenu = new MainMenu();
        this.scanner = new Scanner(System.in);
        this.booksController = new BooksControllerImpl(mainManager);
    };

    @Override
    public boolean run(){
        mainMenu.showMenu();

        while(!checkInput()){
            mainMenu.showMenu();
            checkInput();
        }
        return true;
    }

    @Override
    public boolean checkInput(){
        int answer = scanner.nextInt();
        scanner.nextLine();

        return switch (answer) {
            case 1 -> booksController.run();
            case 4 -> true;
            default -> {
                mainMenu.showInputError();
                yield false;
            }
        };
    }
}
