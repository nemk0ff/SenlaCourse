package controllers.impl;

import annotations.DIComponentDependency;
import controllers.Action;
import constants.IOConstants;
import controllers.impl.IOControllers.ExportController;
import controllers.impl.IOControllers.ImportController;
import controllers.OrdersController;
import manager.MainManagerImpl;
import model.impl.Order;
import model.OrderStatus;
import view.impl.OrdersMenuImpl;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.*;

public class OrdersControllerImpl implements OrdersController {
    @DIComponentDependency
    MainManagerImpl mainManager;
    @DIComponentDependency
    OrdersMenuImpl ordersMenu;

    public OrdersControllerImpl() {
    }

    @Override
    public Action run() {
        ordersMenu.showMenu();
        Action action = checkInput();

        while (action == Action.CONTINUE) {
            ordersMenu.showMenu();
            action = checkInput();
        }

        return action;
    }

    @Override
    public Action checkInput() {
        int answer = (int) getNumberFromConsole();

        return switch (answer) {
            case 1:
                createOrder();
                yield Action.CONTINUE;
            case 2:
                cancelOrder();
                yield Action.CONTINUE;
            case 3:
                showOrderDetails();
                yield Action.CONTINUE;
            case 4:
                setOrderStatus();
                yield Action.CONTINUE;
            case 5:
                getOrdersByDate();
                yield Action.CONTINUE;
            case 6:
                getOrdersByPrice();
                yield Action.CONTINUE;
            case 7:
                getOrdersByStatus();
                yield Action.CONTINUE;
            case 8:
                getCompletedOrdersByDate();
                yield Action.CONTINUE;
            case 9:
                getCompletedOrdersByPrice();
                yield Action.CONTINUE;
            case 10:
                getCountCompletedOrders();
                yield Action.CONTINUE;
            case 11:
                getEarnedSum();
                yield Action.CONTINUE;
            case 12:
                importOrder();
                yield Action.CONTINUE;
            case 13:
                exportOrder();
                yield Action.CONTINUE;
            case 14:
                importAll();
                yield Action.CONTINUE;
            case 15:
                exportAll();
                yield Action.CONTINUE;
            case 16:
                yield Action.MAIN_MENU;
            case 17:
                yield Action.EXIT;
            default:
                ordersMenu.showError("Неизвестная команда");
                yield Action.CONTINUE;
        };
    }

    private String getClientNameFromConsole() {
        ordersMenu.showGetClientName();
        return scanner.nextLine().trim();
    }

    private Map<Long, Integer> getBooksFromConsole() {
        ordersMenu.showBooks(mainManager.getAllBooks());
        ordersMenu.showGetAmountBooks("Сколько уникальных книг вы хотите заказать? Введите число: ");
        int count = (int) getNumberFromConsole();

        long tempId;
        int tempAmount;
        Map<Long, Integer> booksIds = new HashMap<>();

        for (int i = 0; i < count; i++) {
            tempId = getBookFromConsole(i);
            while (!mainManager.containsBook(tempId) || booksIds.containsKey(tempId)) {
                if (!mainManager.containsBook(tempId)) {
                    ordersMenu.showError("Такой книги нет в магазине");
                    ordersMenu.showGetId("Выберите другую книгу и введите её id: ");
                    tempId = getNumberFromConsole();
                }
                if (booksIds.containsKey(tempId)) {
                    ordersMenu.showError("Вы уже выбрали эту книгу");
                    ordersMenu.showGetId("Выберите другую книгу и введите её id: ");
                    tempId = getNumberFromConsole();
                }
            }

            ordersMenu.showGetAmountBooks("Сколько книг [" + tempId + "] вам нужно? Введите число: ");
            tempAmount = (int) getNumberFromConsole();
            booksIds.put(tempId, tempAmount);
        }
        return booksIds;
    }

    private long getBookFromConsole(int index) {
        ordersMenu.showGetBookId(index);
        return getNumberFromConsole();
    }

    @Override
    public void createOrder() {
        try {
            long id = mainManager.createOrder(getBooksFromConsole(), getClientNameFromConsole(), LocalDateTime.now());
            ordersMenu.showSuccess("Заказ создан. id заказа: " + id);
        } catch (Exception e) {
            ordersMenu.showError(e.getMessage());
        }
    }

    @Override
    public void cancelOrder() {
        try {
            ordersMenu.showOrders(mainManager.getAllOrders());
            ordersMenu.showGetId("Введите id заказа, который хотите отменить: ");
            mainManager.cancelOrder(getNumberFromConsole());
            ordersMenu.showSuccess("Заказ отменен");
        } catch (Exception e) {
            ordersMenu.showError(e.getMessage());
        }
    }

    @Override
    public void showOrderDetails() {
        ordersMenu.showGetId("Введите Id заказа: ");
        try {
            Optional<Order> maybeOrder = mainManager.getOrder(getNumberFromConsole());
            if (maybeOrder.isEmpty()) {
                ordersMenu.showError("Заказ не найден");
            } else {
                ordersMenu.showOrder(maybeOrder.get());
            }
        } catch (Exception e) {
            ordersMenu.showError(e.getMessage());
        }
    }

    @Override
    public void setOrderStatus() {
        ordersMenu.showGetId("Введите Id заказа: ");
        long orderId = getNumberFromConsole();

        OrderStatus newStatus = getStatusFromConsole();

        try {
            mainManager.setOrderStatus(orderId, newStatus);
            ordersMenu.showSuccess("Статус заказа изменен");
        } catch (Exception e) {
            ordersMenu.showError(e.getMessage());
        }
    }

    private OrderStatus getStatusFromConsole() {
        ordersMenu.showGetNewStatus();
        String new_status = scanner.nextLine().trim();

        while (!new_status.equalsIgnoreCase(OrderStatus.COMPLETED.toString())
                && !new_status.equalsIgnoreCase(OrderStatus.NEW.toString())
                && !new_status.equalsIgnoreCase(OrderStatus.CANCELED.toString())) {
            ordersMenu.showError("Вы ввели некорректный статус. Попробуйте ещё раз");
            ordersMenu.showGetNewStatus();
            new_status = scanner.nextLine().trim();
        }

        return OrderStatus.valueOf(new_status.toUpperCase());
    }

    @Override
    public void getOrdersByDate() {
        try {
            ordersMenu.showOrders(mainManager.getAllOrdersByDate());
        } catch (Exception e) {
            ordersMenu.showError(e.getMessage());
        }
    }

    @Override
    public void getOrdersByPrice() {
        try {
            ordersMenu.showOrders(mainManager.getAllOrdersByPrice());
        } catch (Exception e) {
            ordersMenu.showError(e.getMessage());
        }
    }

    @Override
    public void getOrdersByStatus() {
        try {
            ordersMenu.showOrders(mainManager.getAllOrdersByStatus());
        } catch (Exception e) {
            ordersMenu.showError(e.getMessage());
        }
    }

    @Override
    public void getCountCompletedOrders() {
        try {
            ordersMenu.showCountCompletedOrders(mainManager.getCountCompletedOrders(getBeginDate(), getEndDate()));
        } catch (Exception e) {
            ordersMenu.showError(e.getMessage());
        }
    }

    @Override
    public void getEarnedSum() {
        try {
            ordersMenu.showEarnedSum(mainManager.getEarnedSum(getBeginDate(), getEndDate()));
        } catch (Exception e) {
            ordersMenu.showError(e.getMessage());
        }
    }

    @Override
    public void getCompletedOrdersByDate() {
        try {
            ordersMenu.showOrders(mainManager.getCompletedOrdersByDate(getBeginDate(), getEndDate()));
        } catch (Exception e) {
            ordersMenu.showError(e.getMessage());
        }
    }

    @Override
    public void getCompletedOrdersByPrice() {
        try {
            ordersMenu.showOrders(mainManager.getCompletedOrdersByPrice(getBeginDate(), getEndDate()));
        } catch (Exception e) {
            ordersMenu.showError(e.getMessage());
        }
    }

    private LocalDateTime getBeginDate() {
        ordersMenu.showGetBeginDate();
        return getDateFromConsole();
    }

    private LocalDateTime getEndDate() {
        ordersMenu.showGetEndDate();
        return getDateFromConsole();
    }

    private LocalDateTime getDateFromConsole() {
        ordersMenu.showGetYear();
        int year = scanner.nextInt();

        ordersMenu.showGetMonth();
        int month = scanner.nextInt();

        ordersMenu.showGetDay();
        int day = scanner.nextInt();

        try {
            return LocalDateTime.of(year, month, day, 0, 0, 0);
        } catch (DateTimeException e) {
            ordersMenu.showError("Некорректный формат даты. Попробуйте ещё раз");
            return getDateFromConsole();
        }
    }

    @Override
    public void importOrder() {
        Optional<Order> findOrder = ImportController.importItem(IOConstants.IMPORT_ORDER_PATH,
                ImportController::orderParser);
        if (findOrder.isPresent()) {
            try {
                mainManager.importItem(findOrder.get());
                ordersMenu.showSuccessImport();
                findOrder.ifPresent(ordersMenu::showItem);
            } catch (Exception e) {
                ordersMenu.showError(e.getMessage());
            }
        } else {
            ordersMenu.showErrorImport();
        }
    }

    @Override
    public void exportOrder() {
        String exportString;
        try {
            ordersMenu.showOrders(mainManager.getAllOrders());
            ordersMenu.showGetId("Введите id заказа, который хотите экспортировать: ");
            long exportId = getNumberFromConsole();

            exportString = getExportString(exportId);
        } catch (IllegalArgumentException e) {
            ordersMenu.showError(e.getMessage());
            return;
        }
        ordersMenu.showOrders(mainManager.getAllOrders());
        ExportController.exportItemToFile(exportString, IOConstants.EXPORT_ORDER_PATH, IOConstants.ORDER_HEADER);
        ordersMenu.showSuccess("Экспорт выполнен успешно");
    }

    @Override
    public void importAll() {
        List<Order> importedOrders = ImportController.importAllItemsFromFile(IOConstants.IMPORT_ORDER_PATH,
                ImportController::orderParser);

        if (!importedOrders.isEmpty()) {
            ordersMenu.showMessage("Результат импортирования: ");
            for (Order importedOrder : importedOrders) {
                try {
                    mainManager.importItem(importedOrder);
                    ordersMenu.showMessage("Импортирован: " + importedOrder.getInfoAbout());
                } catch (Exception e) {
                    ordersMenu.showError("Заказ не импортирован. " + e.getMessage());
                }
            }
        } else {
            ordersMenu.showError("Не удалось импортировать заказы из файла.");
        }
    }

    @Override
    public void exportAll() {
        try {
            ExportController.exportAll(mainManager.getAllOrders(),
                    IOConstants.EXPORT_ORDER_PATH, IOConstants.ORDER_HEADER);
        } catch (Exception e) {
            ordersMenu.showError(e.getMessage());
        }
    }

    public String getExportString(long id) {
        Optional<Order> order = mainManager.getOrder(id);
        if (order.isPresent()) {
            return order.get().toString();
        }
        throw new IllegalArgumentException("Заказ №" + id + " не найден");
    }

    private long getNumberFromConsole() {
        long answer;
        while (true) {
            try {
                answer = InputUtils.getNumberFromConsole();
                break;
            } catch (NumberFormatException e) {
                ordersMenu.showError("Неверный формат, попробуйте еще раз");
            }
        }
        return answer;
    }
}