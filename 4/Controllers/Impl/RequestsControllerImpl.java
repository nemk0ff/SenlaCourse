package Controllers.Impl;

import Controllers.Action;
import Controllers.Controller;
import Controllers.Impl.FileControllers.ExportController;
import Controllers.Impl.FileControllers.ImportController;
import Controllers.RequestsController;
import Model.*;
import Model.Items.Impl.Request;
import Model.Items.RequestStatus;
import View.Menu;
import View.RequestsMenu;
import View.Impl.RequestsMenuImpl;

import java.io.*;
import java.util.*;

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
        int answer = (int) Controller.getNumberFromConsole(requestsMenu);

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
                ImportController.importItem(importPath, requestsMenu, mainManager, ImportController::requestParser);
                yield Action.CONTINUE;
            case 5:
                exportRequest();
                yield Action.CONTINUE;
            case 6:
                importAll();
                yield Action.CONTINUE;
            case 7:
                ExportController.exportAll(requestsMenu, mainManager.getRequests(), exportPath);
                yield Action.CONTINUE;
            case 8:
                yield Action.MAIN_MENU;
            case 9:
                yield Action.EXIT;
            default:
                requestsMenu.showError("Неизвестная команда");
                yield Action.CONTINUE;
        };
    }

    @Override
    public void createRequest() {
        requestsMenu.showBooks(mainManager.getBooks());
        requestsMenu.showGetId("Введите id книги, на которую хотите создать запрос: ");
        long bookId = Controller.getNumberFromConsole(requestsMenu);
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

    @Override
    public void exportRequest() {
        requestsMenu.showGetId("Введите id запроса, который хотите экспортировать: ");
        long exportId = Controller.getNumberFromConsole(requestsMenu);

        String exportString;
        try {
            exportString = getExportString(exportId);
        } catch (IllegalArgumentException e) {
            requestsMenu.showError("Запрос для экспорта не найден");
            return;
        }

        requestsMenu.showRequests(mainManager.getRequests());
        ExportController.exportItemToFile(requestsMenu, exportString, exportPath);
        requestsMenu.showSuccess("Экспорт выполнен успешно");
    }

    @Override
    public void importAll() {
        List<Request> importedRequests = ImportController.importAllItemsFromFile(requestsMenu, importPath, ImportController::requestParser);

        if (!importedRequests.isEmpty()) {
            requestsMenu.showMessage("Результат импортирования:");
            for (Request importedRequest : importedRequests) {
                try {
                    mainManager.importItem(importedRequest);
                    requestsMenu.showMessage("Импортирован: " + importedRequest.getInfoAbout());
                } catch (IllegalArgumentException e) {
                    requestsMenu.showError("Запрос не импортирован. " + e.getMessage());
                }
            }
        } else {
            requestsMenu.showError("Не удалось импортировать запросы из файла.");
        }
    }

    public String getExportString(long id) {
        Optional<Request> request = mainManager.getRequest(id);
        if (request.isPresent()) {
            return request.get().toString();
        }
        throw new IllegalArgumentException();
    }
}
