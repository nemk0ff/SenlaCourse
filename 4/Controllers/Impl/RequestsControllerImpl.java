package Controllers.Impl;

import Controllers.Action;
import Controllers.RequestsController;
import Model.Book;
import Model.MainManager;
import View.RequestsMenu;
import View.Impl.RequestsMenuImpl;

public class RequestsControllerImpl implements RequestsController {
    private final MainManager mainManager;
    private final RequestsMenu requestsMenu;

    public RequestsControllerImpl(MainManager mainManager) {
        this.mainManager = mainManager;
        this.requestsMenu = new RequestsMenuImpl();
    }

    @Override
    public Action run() {
        requestsMenu.showMenu();
        Action action = checkInput();

        while (action == Action.CONTINUE) {
            requestsMenu.showMenu();
            action = checkInput();
        }

        return action;
    }

    @Override
    public Action checkInput() {
        int answer = (int) getNumberFromConsole(requestsMenu);

        return switch (answer) {
            case 1:
                createRequest();
                yield Action.CONTINUE;
            case 2:
                getRequestsByCount();
                yield Action.CONTINUE;
            case 3:
                getRequestsByPrice();
                yield Action.CONTINUE;
            case 4:
                yield Action.MAIN_MENU;
            case 5:
                yield Action.EXIT;
            default:
                requestsMenu.showError("Неизвестная команда");
                yield Action.CONTINUE;
        };
    }

    @Override
    public void createRequest() {
        requestsMenu.showBooks(mainManager.getBooks());
        long bookId = getBookFromConsole(requestsMenu);
        mainManager.addRequest(bookId);
    }

    @Override
    public void getRequestsByCount() {
        requestsMenu.showRequests(mainManager.getRequestsByCount());
    }

    @Override
    public void getRequestsByPrice() {
        requestsMenu.showRequests(mainManager.getRequestsByPrice());
    }
}
