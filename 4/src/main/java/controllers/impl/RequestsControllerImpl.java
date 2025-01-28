package controllers.impl;

import annotations.DIComponentDependency;
import controllers.Action;
import constants.IOConstants;
import controllers.impl.IOControllers.ExportController;
import controllers.impl.IOControllers.ImportController;
import controllers.RequestsController;
import manager.MainManagerImpl;
import model.impl.Request;
import view.impl.RequestsMenuImpl;

import java.util.*;

public class RequestsControllerImpl implements RequestsController {
    @DIComponentDependency
    MainManagerImpl mainManager;
    @DIComponentDependency
    RequestsMenuImpl requestsMenu;

    public RequestsControllerImpl() {
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
        int answer = (int) getNumberFromConsole();

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
                importRequest();
                yield Action.CONTINUE;
            case 5:
                exportRequest();
                yield Action.CONTINUE;
            case 6:
                importAll();
                yield Action.CONTINUE;
            case 7:
                exportAll();
                yield Action.CONTINUE;
            case 8:
                getAllRequests();
                yield Action.CONTINUE;
            case 9:
                yield Action.MAIN_MENU;
            case 10:
                yield Action.EXIT;
            default:
                requestsMenu.showError("Неизвестная команда");
                yield Action.CONTINUE;
        };
    }

    @Override
    public void createRequest() {
        requestsMenu.showBooks(mainManager.getAllBooks());
        requestsMenu.showGetId("Введите id книги, на которую хотите создать запрос: ");
        long bookId = getNumberFromConsole();
        requestsMenu.showMessage("На сколько книг создать запрос? ");
        int amount = (int) getNumberFromConsole();
        try {
            mainManager.createRequest(bookId, amount);
        } catch (Exception e) {
            requestsMenu.showError(e.getMessage());
        }
    }

    @Override
    public void getRequestsByCount() {
        try {
            requestsMenu.showRequests(mainManager.getRequestsByCount());
        } catch (Exception e) {
            requestsMenu.showError(e.getMessage());
        }
    }

    @Override
    public void getRequestsByPrice() {
        try {
            requestsMenu.showRequests(mainManager.getRequestsByPrice());
        } catch (Exception e) {
            requestsMenu.showError(e.getMessage());
        }
    }

    @Override
    public void getAllRequests() {
        try {
            requestsMenu.showRequests(mainManager.getRequests());
        } catch (Exception e) {
            requestsMenu.showError(e.getMessage());
        }
    }

    @Override
    public void importRequest() {
        Optional<Request> findRequest = ImportController.importItem(IOConstants.IMPORT_REQUEST_PATH,
                ImportController::requestParser);
        if (findRequest.isPresent()) {
            try {
                mainManager.importItem(findRequest.get());
                requestsMenu.showSuccessImport();
                findRequest.ifPresent(requestsMenu::showItem);
            } catch (Exception e) {
                requestsMenu.showError(e.getMessage());
            }
        } else {
            requestsMenu.showErrorImport();
        }
    }

    @Override
    public void exportRequest() {
        requestsMenu.showRequests(mainManager.getRequests());
        requestsMenu.showGetId("Введите id запроса, который хотите экспортировать: ");
        long exportId = getNumberFromConsole();

        String exportString;
        try {
            exportString = getExportString(exportId);
        } catch (Exception e) {
            requestsMenu.showError("Запрос для экспорта не найден");
            return;
        }

        ExportController.exportItemToFile(exportString, IOConstants.EXPORT_REQUEST_PATH, IOConstants.REQUEST_HEADER);
        requestsMenu.showSuccess("Экспорт выполнен успешно");
    }

    @Override
    public void importAll() {
        List<Request> importedRequests = ImportController.importAllItemsFromFile(IOConstants.IMPORT_REQUEST_PATH,
                ImportController::requestParser);

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
            requestsMenu.showError("Импорт не выполнен: запросы для импорта не найдены");
        }
    }

    @Override
    public void exportAll() {
        try {
            ExportController.exportAll(mainManager.getRequests(),
                    IOConstants.EXPORT_REQUEST_PATH, IOConstants.REQUEST_HEADER);
        } catch (Exception e) {
            requestsMenu.showError("Не удалось выполнить экспорт: " + e.getMessage());
        }
    }

    public String getExportString(long id) {
        Optional<Request> request = mainManager.getRequest(id);
        if (request.isPresent()) {
            return request.get().toString();
        }
        throw new IllegalArgumentException("Запрос №" + id + " не найден");
    }

    private long getNumberFromConsole() {
        long answer;
        while (true) {
            try {
                answer = InputUtils.getNumberFromConsole();
                break;
            } catch (NumberFormatException e) {
                requestsMenu.showError("Неверный формат, попробуйте еще раз");
            }
        }
        return answer;
    }
}
