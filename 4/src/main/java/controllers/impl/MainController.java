package controllers.impl;

import DI.DI;
import annotations.DIComponentDependency;
import config.DeserializationManager;
import config.SerializationManager;
import controllers.Action;
import controllers.Controller;
import managers.MainManager;
import view.impl.MainMenu;


public class MainController implements Controller {
    @DIComponentDependency
    DI di;
    @DIComponentDependency
    MainMenu mainMenu;
    @DIComponentDependency
    BooksControllerImpl booksController;
    @DIComponentDependency
    OrdersControllerImpl ordersController;
    @DIComponentDependency
    RequestsControllerImpl requestsController;
    @DIComponentDependency
    DeserializationManager deserializationManager;
    @DIComponentDependency
    SerializationManager serializationManager;

    public MainController() {
    }

    @Override
    public Action run() {
        MainManager deserializeObj = deserializationManager.deserialize();
        saveMainManager(deserializeObj);

        mainMenu.showMenu();
        Action action = checkInput();

        while (action == Action.CONTINUE || action == Action.MAIN_MENU) {
            mainMenu.showMenu();
            action = checkInput();
        }

        serializationManager.serialize(mainManager());
        return Action.EXIT;
    }

    private MainManager mainManager() {
        return di.getBean(MainManager.class);
    }

    private void saveMainManager(MainManager mainManager) {
        di.registerBean(MainManager.class, () -> mainManager);
    }

    @Override
    public Action checkInput() {
        int answer = (int) getNumberFromConsole();

        return switch (answer) {
            case 1 -> booksController.run();
            case 2 -> ordersController.run();
            case 3 -> requestsController.run();
            case 4 -> Action.EXIT;
            default -> {
                mainMenu.showError("Неизвестная команда");
                yield Action.CONTINUE;
            }
        };
    }

    private long getNumberFromConsole() {
        long answer;
        while (true) {
            try {
                answer = InputUtils.getNumberFromConsole();
                break;
            } catch (NumberFormatException e) {
                mainMenu.showError("Неверный формат, попробуйте еще раз");
            }
        }
        return answer;
    }
}
