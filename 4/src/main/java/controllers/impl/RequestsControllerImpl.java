package controllers.impl;

import DI.DI;
import annotations.DIComponent;
import annotations.DIComponentDependency;
import controllers.Action;
import constants.IOConstants;
import controllers.impl.IOControllers.ExportController;
import controllers.impl.IOControllers.ImportController;
import controllers.RequestsController;
import managers.MainManager;
import model.impl.Request;
import view.RequestsMenu;

import java.util.*;

@DIComponent
public class RequestsControllerImpl implements RequestsController {
    @DIComponentDependency
    DI di;
    @DIComponentDependency
    RequestsMenu requestsMenu;

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
                ExportController.exportAll(mainManager().getRequests(),
                        IOConstants.EXPORT_REQUEST_PATH, IOConstants.REQUEST_HEADER);
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
        MainManager mainManager = mainManager();

        requestsMenu.showBooks(mainManager.getBooks());
        requestsMenu.showGetId("Введите id книги, на которую хотите создать запрос: ");
        long bookId = getNumberFromConsole();
        requestsMenu.showMessage("На сколько книг создать запрос? ");
        int amount = (int) getNumberFromConsole();
        mainManager.addRequest(bookId, amount);

        saveMainManager(mainManager);
    }

    @Override
    public void getRequestsByCount() {
        requestsMenu.showRequests(mainManager().getRequestsByCount());
    }

    @Override
    public void getRequestsByPrice() {
        requestsMenu.showRequests(mainManager().getRequestsByPrice());
    }

    @Override
    public void getAllRequests() {
        requestsMenu.showRequests(mainManager().getRequests());
    }

    @Override
    public void importRequest() {
        MainManager mainManager = mainManager();

        Optional<Request> findRequest = ImportController.importItem(IOConstants.IMPORT_REQUEST_PATH,
                ImportController::requestParser);
        if (findRequest.isPresent()) {
            try {
                mainManager.importItem(findRequest.get());
                requestsMenu.showSuccessImport();
                findRequest.ifPresent(requestsMenu::showItem);
            } catch (IllegalArgumentException e) {
                requestsMenu.showError(e.getMessage());
            }
        } else {
            requestsMenu.showErrorImport();
        }

        saveMainManager(mainManager);
    }

    @Override
    public void exportRequest() {
        requestsMenu.showRequests(mainManager().getRequests());
        requestsMenu.showGetId("Введите id запроса, который хотите экспортировать: ");
        long exportId = getNumberFromConsole();

        String exportString;
        try {
            exportString = getExportString(exportId);
        } catch (IllegalArgumentException e) {
            requestsMenu.showError("Запрос для экспорта не найден");
            return;
        }

        ExportController.exportItemToFile(exportString, IOConstants.EXPORT_REQUEST_PATH, IOConstants.REQUEST_HEADER);
        requestsMenu.showSuccess("Экспорт выполнен успешно");
    }

    @Override
    public void importAll() {
        MainManager mainManager = mainManager();

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
            requestsMenu.showError("Не удалось импортировать запросы из файла.");
        }

        saveMainManager(mainManager);
    }

    public String getExportString(long id) {
        Optional<Request> request = mainManager().getRequest(id);
        if (request.isPresent()) {
            return request.get().toString();
        }
        throw new IllegalArgumentException();
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
