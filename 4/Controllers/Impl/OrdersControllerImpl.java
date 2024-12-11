package Controllers.Impl;

import Controllers.Action;
import Controllers.OrdersController;
import Model.Book;
import Model.MainManager;
import Model.Order;
import Model.OrderStatus;
import View.Impl.OrdersMenuImpl;
import View.OrdersMenu;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        int answer;
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                answer = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                ordersMenu.showError("Неверный формат, попробуйте еще раз");
            }
        }

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
                yield Action.MAIN_MENU;
            case 13:
                yield Action.EXIT;
            default:
                ordersMenu.showError("Неизвестная команда");
                yield Action.CONTINUE;
        };
    }

    @Override
    public String getClientNameFromConsole(){
        ordersMenu.showGetClientName();
        return scanner.nextLine().trim();
    }

    @Override
    public List<Book> getBooksFromConsole(){
        ordersMenu.showBooks(mainManager.getBooks());
        int count;
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                count = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                ordersMenu.showError("Неверный формат, попробуйте еще раз");
            }
        }

        Book tempBook;
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ordersMenu.showGetBook(i);
            tempBook = getBookFromConsole(ordersMenu);
            while(!mainManager.containsBook(tempBook)){
                ordersMenu.showError("Такой книги нет в магазине");
                tempBook = getBookFromConsole(ordersMenu);
            }
            for (Book book: mainManager.getBooks()) {
                if(book.equals(tempBook)){
                    books.add(book);
                }
            }
        }
        return books;
    }

    @Override
    public void createOrder() {
        mainManager.createOrder(getBooksFromConsole(), getClientNameFromConsole(), LocalDate.now());
    }

    @Override
    public void cancelOrder() {
        mainManager.cancelOrder(new Order(getBooksFromConsole(), getClientNameFromConsole()));
    }

    @Override
    public void showOrderDetails() {
        Optional<Order> maybeOrder = mainManager.getOrderDetails(getClientNameFromConsole(), getBooksFromConsole());
        if (maybeOrder.isEmpty()) {
            ordersMenu.showError("Заказ не найден");
        } else {
            ordersMenu.showOrder(maybeOrder.get());
        }
    }

    @Override
    public void setOrderStatus() {
        OrderStatus newStatus = getStatusFromConsole();

        mainManager.setOrderStatus(new Order(getBooksFromConsole(), getClientNameFromConsole()), newStatus);
    }

    @Override
    public OrderStatus getStatusFromConsole() {
        ordersMenu.showGetNewStatus();
        String new_status = scanner.nextLine().trim();

        while (!Objects.equals(new_status, OrderStatus.COMPLETED.toString())
                && !Objects.equals(new_status, OrderStatus.CANCELED.toString())
                && !Objects.equals(new_status, OrderStatus.NEW.toString())) {
            ordersMenu.showError("Вы ввели некорректный статус. Попробуйте ещё раз");
            ordersMenu.showGetNewStatus();
            new_status = scanner.nextLine().trim();
        }

        return OrderStatus.valueOf(new_status);
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
}