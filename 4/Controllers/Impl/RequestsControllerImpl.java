package Controllers.Impl;

import Controllers.Action;
import Controllers.RequestsController;
import Model.*;
import View.Menu;
import View.RequestsMenu;
import View.Impl.RequestsMenuImpl;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                importRequest();
                yield Action.CONTINUE;
            case 5:
                exportRequest();
                yield Action.CONTINUE;
            case 6:
                yield Action.MAIN_MENU;
            case 7:
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

    @Override
    public void importRequest() {
        printImportFile();

        requestsMenu.showMessage("Введите id запроса, который хотите импортировать");
        long requestId = getNumberFromConsole(requestsMenu);

        Optional<Request> findRequest = findRequestInFile(requestId);

        if (findRequest.isPresent()) {
            try{
                mainManager.importRequest(findRequest.get());
                requestsMenu.showMessage("Запрос успешно импортирован:");
                findRequest.ifPresent(requestsMenu::showRequest);
            } catch (IllegalArgumentException e){
                requestsMenu.showError(e.getMessage());
            }
        } else {
            requestsMenu.showError("Не удалось получить запрос из файла");
        }
    }

    public Optional<Request> findRequestInFile(Long targetRequestId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(importPath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    throw new IllegalArgumentException("Обнаружена строка неверного формата: " + line);
                }

                long id = Long.parseLong(parts[0].trim());
                if (id == targetRequestId) {
                    long bookId = Long.parseLong(parts[1].trim());
                    RequestStatus status = RequestStatus.valueOf(parts[2].trim());

                    return Optional.of(new Request(id, bookId, status));
                }
            }
        } catch (IOException e) {
            requestsMenu.showError("IOException");
        }
        return Optional.empty();
    }

    private void printImportFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(importPath))) {
            requestsMenu.showMessage("Вот, какие запросы можно импортировать: ");
            String line;
            while ((line = reader.readLine()) != null) {
                requestsMenu.showMessage("[" + line + "]");
            }
        } catch (IOException e) {
            System.err.println(importPath + ": " + e.getMessage());
        }
    }

    @Override
    public void exportRequest() {
        requestsMenu.showRequests(mainManager.getRequests());
        long exportId = getRequestId();
        String exportString = "";
        if(mainManager.getRequest(exportId).isPresent()){
            exportString = mainManager.getRequest(exportId).get().toString();
        }

        List<String> newFileStrings = new ArrayList<>();

        String firstString = "id;bookId;status";
        newFileStrings.add(firstString);

        boolean requestIsUpdated = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(exportPath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                long id = Long.parseLong(parts[0].trim());
                if (id == exportId) {
                    newFileStrings.add(exportString);
                    requestIsUpdated = true;
                } else {
                    newFileStrings.add(line);
                }
            }
        } catch (IOException e) {
            requestsMenu.showError("IOException: " + e.getMessage());
            return;
        }

        if (!requestIsUpdated) {
            newFileStrings.add(exportString);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportPath))) {
            for (String line : newFileStrings) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            requestsMenu.showError("IOException: " + e.getMessage());
        }

        requestsMenu.showSuccess("Запрос успешно экспортирован");
    }

    private long getRequestId() {
        long requestId = getRequestFromConsole(requestsMenu);
        while (!mainManager.containsRequest(requestId)) {
            requestsMenu.showError("Такого запроса нет в магазине");
            requestId = getBookFromConsole(requestsMenu);
        }
        return requestId;
    }

    @Override
    public long getRequestFromConsole(Menu menu) {
        menu.showGetId("Введите id запроса: ");
        return getNumberFromConsole(menu);
    }
}
