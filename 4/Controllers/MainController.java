package Controllers;

import Model.MainManager;
import Model.MainManagerImpl;
import View.MainMenu;

public class MainController implements Controller {
    private final MainMenu mainMenu;
    private final BooksControllerImpl booksController;
    private final OrdersControllerImpl ordersController;
    private final RequestsController requestsController;


    public MainController() {
        MainManager mainManager = new MainManagerImpl();
        this.mainMenu = new MainMenu();
        this.booksController = new BooksControllerImpl(mainManager);
        this.ordersController = new OrdersControllerImpl(mainManager);
        this.requestsController = new RequestsControllerImpl(mainManager);
    }

    @Override
    public Action run() {
        mainMenu.showMenu();
        Action action = checkInput();

        while (action == Action.CONTINUE || action == Action.MAIN_MENU) {
            mainMenu.showMenu();
            action = checkInput();
        }

        return Action.EXIT;
    }

    @Override
    public Action checkInput() {
        int answer;
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                answer = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                mainMenu.showError("Неверный формат, попробуйте еще раз");
            }
        }

        return switch (answer) {
            case 1 -> booksController.run();
            case 2 -> ordersController.run();
            case 3 -> requestsController.run();
            case 4 -> Action.EXIT;
            default -> {
                mainMenu.showInputError();
                yield Action.CONTINUE;
            }
        };
    }
}
