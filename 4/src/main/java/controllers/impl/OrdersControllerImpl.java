package controllers.impl;

import DI.DI;
import annotations.DIComponentDependency;
import controllers.Action;
import constants.IOConstants;
import controllers.impl.IOControllers.ExportController;
import controllers.impl.IOControllers.ImportController;
import controllers.OrdersController;
import managers.MainManager;
import model.impl.Order;
import model.OrderStatus;
import view.impl.OrdersMenuImpl;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;

public class OrdersControllerImpl implements OrdersController {
    @DIComponentDependency
    DI di;
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
                ExportController.exportAll(mainManager().getOrders(),
                        IOConstants.EXPORT_ORDER_PATH, IOConstants.ORDER_HEADER);
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

    @Override
    public String getClientNameFromConsole() {
        ordersMenu.showGetClientName();
        return scanner.nextLine().trim();
    }

    @Override
    public Map<Long, Integer> getBooksFromConsole() {
        ordersMenu.showBooks(mainManager().getBooks());
        ordersMenu.showGetAmountBooks("Сколько уникальных книг вы хотите заказать? Введите число: ");
        int count = (int) getNumberFromConsole();

        long tempId;
        int tempAmount;
        Map<Long, Integer> booksIds = new HashMap<>();

        for (int i = 0; i < count; i++) {
            tempId = getBookFromConsole(i);
            while (!mainManager().containsBook(tempId) || booksIds.containsKey(tempId)) {
                if (!mainManager().containsBook(tempId)) {
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

    @Override
    public long getBookFromConsole(int index) {
        ordersMenu.showGetBookId(index);
        return getNumberFromConsole();
    }

    @Override
    public void createOrder() {
        MainManager mainManager = mainManager();

        mainManager.createOrder(getBooksFromConsole(), getClientNameFromConsole(), LocalDate.now());

        saveMainManager(mainManager);
    }

    @Override
    public void cancelOrder() {
        MainManager mainManager = mainManager();

        ordersMenu.showOrders(mainManager.getOrders());
        ordersMenu.showGetId("Введите id заказа, который хотите отменить: ");
        if (mainManager.cancelOrder(getNumberFromConsole())) {
            ordersMenu.showSuccess("Заказ отменен");
        } else {
            ordersMenu.showError("С таким id нет заказа, который можно отменить");
        }

        saveMainManager(mainManager);
    }

    @Override
    public void showOrderDetails() {
        ordersMenu.showGetId("Введите Id заказа: ");
        Optional<Order> maybeOrder = mainManager().getOrder(getNumberFromConsole());
        if (maybeOrder.isEmpty()) {
            ordersMenu.showError("Заказ не найден");
        } else {
            ordersMenu.showOrder(maybeOrder.get());
        }
    }

    @Override
    public void setOrderStatus() {
        MainManager mainManager = mainManager();

        ordersMenu.showGetId("Введите Id заказа: ");
        long orderId = getNumberFromConsole();

        OrderStatus newStatus = getStatusFromConsole();

        if (mainManager.setOrderStatus(orderId, newStatus)) {
            ordersMenu.showSuccess("Статус заказа изменен");
        } else {
            ordersMenu.showError("Статус заказа не изменен. Статус можно менять только с NEW на не NEW");
        }

        saveMainManager(mainManager);
    }

    @Override
    public OrderStatus getStatusFromConsole() {
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
        ordersMenu.showOrders(mainManager().getOrdersByDate());
    }

    @Override
    public void getOrdersByPrice() {
        ordersMenu.showOrders(mainManager().getOrdersByPrice());
    }

    @Override
    public void getOrdersByStatus() {
        ordersMenu.showOrders(mainManager().getOrdersByStatus());
    }

    @Override
    public void getCountCompletedOrders() {
        ordersMenu.showCountCompletedOrders(mainManager().getCountCompletedOrders(getBeginDate(), getEndDate()));
    }

    @Override
    public void getEarnedSum() {
        ordersMenu.showEarnedSum(mainManager().getEarnedSum(getBeginDate(), getEndDate()));
    }

    @Override
    public void getCompletedOrdersByDate() {
        ordersMenu.showOrders(mainManager().getCompletedOrdersByDate(getBeginDate(), getEndDate()));
    }

    @Override
    public void getCompletedOrdersByPrice() {
        ordersMenu.showOrders(mainManager().getCompletedOrdersByPrice(getBeginDate(), getEndDate()));
    }

    @Override
    public LocalDate getBeginDate() {
        ordersMenu.showGetBeginDate();
        return getDateFromConsole();
    }

    @Override
    public LocalDate getEndDate() {
        ordersMenu.showGetEndDate();
        return getDateFromConsole();
    }

    @Override
    public LocalDate getDateFromConsole() {
        ordersMenu.showGetYear();
        int year = scanner.nextInt();

        ordersMenu.showGetMonth();
        int month = scanner.nextInt();

        ordersMenu.showGetDay();
        int day = scanner.nextInt();

        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            ordersMenu.showError("Некорректный формат даты. Попробуйте ещё раз");
            return getDateFromConsole();
        }
    }

    @Override
    public void importOrder() {
        MainManager mainManager = mainManager();

        Optional<Order> findOrder = ImportController.importItem(IOConstants.IMPORT_ORDER_PATH,
                ImportController::orderParser);
        if (findOrder.isPresent()) {
            try {
                mainManager.importItem(findOrder.get());
                ordersMenu.showSuccessImport();
                findOrder.ifPresent(ordersMenu::showItem);
            } catch (IllegalArgumentException e) {
                ordersMenu.showError(e.getMessage());
            }
        } else {
            ordersMenu.showErrorImport();
        }

        saveMainManager(mainManager);
    }

    @Override
    public void exportOrder() {
        ordersMenu.showOrders(mainManager().getOrders());
        ordersMenu.showGetId("Введите id заказа, который хотите экспортировать: ");
        long exportId = getNumberFromConsole();

        String exportString;
        try {
            exportString = getExportString(exportId);
        } catch (IllegalArgumentException e) {
            ordersMenu.showError("Заказ для экспорта не найден");
            return;
        }

        ordersMenu.showOrders(mainManager().getOrders());
        ExportController.exportItemToFile(exportString, IOConstants.EXPORT_ORDER_PATH, IOConstants.ORDER_HEADER);
        ordersMenu.showSuccess("Экспорт выполнен успешно");
    }

    @Override
    public void importAll() {
        MainManager mainManager = mainManager();

        List<Order> importedOrders = ImportController.importAllItemsFromFile(IOConstants.IMPORT_ORDER_PATH,
                ImportController::orderParser);

        if (!importedOrders.isEmpty()) {
            ordersMenu.showMessage("Результат импортирования: ");
            for (Order importedOrder : importedOrders) {
                try {
                    mainManager.importItem(importedOrder);
                    ordersMenu.showMessage("Импортирован: " + importedOrder.getInfoAbout());
                } catch (IllegalArgumentException e) {
                    ordersMenu.showError("Заказ не импортирован. " + e.getMessage());
                }
            }
        } else {
            ordersMenu.showError("Не удалось импортировать заказы из файла.");
        }

        saveMainManager(mainManager);
    }

    public String getExportString(long id) {
        Optional<Order> order = mainManager().getOrder(id);
        if (order.isPresent()) {
            return order.get().toString();
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
                ordersMenu.showError("Неверный формат, попробуйте еще раз");
            }
        }
        return answer;
    }
}