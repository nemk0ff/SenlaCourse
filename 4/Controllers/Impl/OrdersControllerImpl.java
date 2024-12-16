package Controllers.Impl;

import Controllers.Action;
import Controllers.OrdersController;
import Model.MainManager;
import Model.Order;
import Model.OrderStatus;
import View.Menu;
import View.OrdersMenu;
import View.Impl.OrdersMenuImpl;

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrdersControllerImpl implements OrdersController {
    private final MainManager mainManager;
    private final OrdersMenu ordersMenu;

    public OrdersControllerImpl(MainManager mainManager) {
        this.mainManager = mainManager;
        this.ordersMenu = new OrdersMenuImpl();
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
        int answer = (int) getNumberFromConsole(ordersMenu);

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
                importFromFile();
                yield Action.CONTINUE;
            case 13:
                exportToFile();
                yield Action.CONTINUE;
            case 14:
                yield Action.MAIN_MENU;
            case 15:
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
        ordersMenu.showBooks(mainManager.getBooks());
        ordersMenu.showGetAmountBooks("Сколько уникальных книг вы хотите заказать? Введите число: ");
        int count = (int) getNumberFromConsole(ordersMenu);

        long tempId;
        int tempAmount;
        Map<Long, Integer> booksIds = new HashMap<>();

        for (int i = 0; i < count; i++) {
            tempId = getBookFromConsole(ordersMenu, i);
            while (!mainManager.containsBook(tempId) || booksIds.containsKey(tempId)) {
                if (!mainManager.containsBook(tempId)) {
                    ordersMenu.showError("Такой книги нет в магазине");
                    tempId = getBookFromConsole(ordersMenu);
                }
                if (booksIds.containsKey(tempId)) {
                    ordersMenu.showError("Вы уже выбрали эту книгу");
                    tempId = getBookFromConsole(ordersMenu);
                }
            }

            ordersMenu.showGetAmountBooks("Сколько книг [" + tempId + "] вам нужно? Введите число: ");
            tempAmount = (int) getNumberFromConsole(ordersMenu);
            booksIds.put(tempId, tempAmount);
        }
        return booksIds;
    }

    @Override
    public void createOrder() {
        mainManager.createOrder(getBooksFromConsole(), getClientNameFromConsole(), LocalDate.now());
    }

    @Override
    public void cancelOrder() {
        ordersMenu.showOrders(mainManager.getOrders());
        ordersMenu.showGetId("Введите id заказа, который хотите отменить: ");
        if (mainManager.cancelOrder(getNumberFromConsole(ordersMenu))) {
            ordersMenu.showSuccess("Заказ отменен");
        } else {
            ordersMenu.showError("С таким id нет заказа, который можно отменить");
        }
    }

    @Override
    public void showOrderDetails() {
        ordersMenu.showGetId("Введите Id заказа: ");
        Optional<Order> maybeOrder = mainManager.getOrder(getNumberFromConsole(ordersMenu));
        if (maybeOrder.isEmpty()) {
            ordersMenu.showError("Заказ не найден");
        } else {
            ordersMenu.showOrder(maybeOrder.get());
        }
    }

    @Override
    public void setOrderStatus() {
        ordersMenu.showGetId("Введите Id заказа: ");
        long orderId = getNumberFromConsole(ordersMenu);

        OrderStatus newStatus = getStatusFromConsole();

        if (mainManager.setOrderStatus(orderId, newStatus)) {
            ordersMenu.showSuccess("Статус заказа изменен");
        } else {
            ordersMenu.showError("Статус заказа не изменен. Статус можно менять только с NEW на не NEW");
        }
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
        ordersMenu.showOrders(mainManager.getOrdersByDate());
    }

    @Override
    public void getOrdersByPrice() {
        ordersMenu.showOrders(mainManager.getOrdersByPrice());
    }

    @Override
    public void getOrdersByStatus() {
        ordersMenu.showOrders(mainManager.getOrdersByStatus());
    }

    @Override
    public void getCountCompletedOrders() {
        ordersMenu.showCountCompletedOrders(mainManager.getCountCompletedOrders(getBeginDate(), getEndDate()));
    }

    @Override
    public void getEarnedSum() {
        ordersMenu.showEarnedSum(mainManager.getEarnedSum(getBeginDate(), getEndDate()));
    }

    @Override
    public void getCompletedOrdersByDate() {
        ordersMenu.showOrders(mainManager.getCompletedOrdersByDate(getBeginDate(), getEndDate()));
    }

    @Override
    public void getCompletedOrdersByPrice() {
        ordersMenu.showOrders(mainManager.getCompletedOrdersByPrice(getBeginDate(), getEndDate()));
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
    public void importFromFile() {
        printImportFile();

        ordersMenu.showMessage("Введите id заказа, который хотите импортировать");
        long orderId = getNumberFromConsole(ordersMenu);

        Optional<Order> findOrder = findOrderInFile(orderId);

        if (findOrder.isPresent()) {
            try{
                mainManager.importOrder(findOrder.get());
                ordersMenu.showMessage("Заказ успешно импортирован:");
                findOrder.ifPresent(ordersMenu::showOrder);
            } catch (IllegalArgumentException e){
                ordersMenu.showError(e.getMessage());
            }
        } else {
            ordersMenu.showError("Не удалось получить заказ из файла");
        }
    }

    public Optional<Order> findOrderInFile(Long targetOrderId) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (BufferedReader reader = new BufferedReader(new FileReader(importPath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 6 || parts.length % 2 != 0) {
                    throw new IllegalArgumentException("Обнаружена строка неверного формата: " + line);
                }

                long id = Long.parseLong(parts[0].trim());
                if (id == targetOrderId) {
                    String name = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    OrderStatus status = OrderStatus.valueOf(parts[3].trim());
                    LocalDate orderDate = parts[4].trim().equals("null") ?
                            null : LocalDate.parse(parts[4].trim(), dateFormatter);
                    LocalDate completeDate = parts[5].trim().equals("null") ?
                            null : LocalDate.parse(parts[5].trim(), dateFormatter);

                    Map<Long, Integer> books = new HashMap<>();
                    for (int i = 6; i < parts.length; i += 2) {
                        long bookId = Long.parseLong(parts[i].trim());
                        int amount = Integer.parseInt(parts[i + 1].trim());
                        books.put(bookId, amount);
                    }
                    return Optional.of(new Order(id, name, price, status, orderDate, completeDate, books));
                }
            }
        } catch (IOException e) {
            ordersMenu.showError("IOException");
        }
        return Optional.empty();
    }

    public void printImportFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(importPath))) {
            ordersMenu.showMessage("Вот, какие заказы можно импортировать: ");
            String line;
            while ((line = reader.readLine()) != null) {
                ordersMenu.showMessage("[" + line + "]");
            }
        } catch (IOException e) {
            System.err.println(importPath + ": " + e.getMessage());
        }
    }

    @Override
    public void exportToFile() {
        ordersMenu.showOrders(mainManager.getOrders());
        long exportId = getOrderId();
        String exportString = mainManager.getOrder(exportId).toString();

        List<String> newFileStrings = new ArrayList<>();

        String firstString = "id;clientName;price;status;orderDate;completeDate;book1;amount1;book2;amount2;...;bookN;amountN";
        newFileStrings.add(firstString);

        boolean orderIsUpdated = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(exportPath))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                long id = Long.parseLong(parts[0].trim());
                if (id == exportId) {
                    newFileStrings.add(exportString);
                    orderIsUpdated = true;
                } else {
                    newFileStrings.add(line);
                }
            }
        } catch (Exception e) {
            ordersMenu.showError("Error: " + e.getMessage());
            return;
        }

        if (!orderIsUpdated) {
            newFileStrings.add(exportString);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportPath))) {
            for (String line : newFileStrings) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            ordersMenu.showError("IOException: " + e.getMessage());
        }

        ordersMenu.showSuccess("Заказ успешно экспортирован");
    }

    private long getOrderId() {
        long orderId = getOrderFromConsole(ordersMenu);
        while (!mainManager.containsOrder(orderId)) {
            ordersMenu.showError("Такого заказа нет в магазине");
            orderId = getOrderFromConsole(ordersMenu);
        }
        return orderId;
    }

    @Override
    public long getOrderFromConsole(Menu menu) {
        menu.showGetId("Введите id заказа: ");
        return getNumberFromConsole(menu);
    }
}