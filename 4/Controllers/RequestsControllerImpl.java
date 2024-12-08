package Controllers;

import Model.Book;
import Model.MainManager;
import View.RequestsMenu;
import View.RequestsMenuImpl;

public class RequestsControllerImpl implements RequestsController{
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

        while(action == Action.CONTINUE){
            requestsMenu.showMenu();
            action = checkInput();
        }

        return action;
    }

    @Override
    public Action checkInput() {
        int answer;
        while (true) {
            String input = scanner.nextLine();
            try {
                answer = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                requestsMenu.showError("Неверный формат, попробуйте еще раз");
            }
        }

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
                requestsMenu.showInputError();
                yield Action.CONTINUE;
        };
    }

    @Override
    public void createRequest() {
        Book book = getBookFromConsole(requestsMenu);
        mainManager.addRequest(book);
    }

    @Override
    public void getRequestsByCount() { requestsMenu.showRequests(mainManager.getRequestsByCount()); }

    @Override
    public void getRequestsByPrice() { requestsMenu.showRequests(mainManager.getRequestsByPrice()); }
}
