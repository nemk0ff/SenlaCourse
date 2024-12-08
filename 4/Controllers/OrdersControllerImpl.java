package Controllers;

import Model.Book;
import Model.MainManager;
import Model.Order;
import Model.OrderStatus;
import View.OrdersMenu;
import View.OrdersMenuImpl;

import java.time.DateTimeException;
import java.time.LocalDate;
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

        while(action == Action.CONTINUE){
            ordersMenu.showMenu();
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
            case 12 : yield Action.MAIN_MENU;
            case 13 : yield Action.EXIT;
            default:
                ordersMenu.showInputError();
                yield Action.CONTINUE;
        };
    }

    @Override
    public void createOrder() {
        Order inputOrder = getOrderFromConsole();
        mainManager.createOrder(inputOrder.getBook(), inputOrder.getClientName(), LocalDate.now());
    }

    @Override
    public void cancelOrder() { mainManager.cancelOrder(getOrderFromConsole()); }

    @Override
    public void showOrderDetails() {
        Order inputOrder = getOrderFromConsole();

        Optional<Order> maybeOrder = mainManager.getOrderDetails(inputOrder.getClientName(), inputOrder.getBook());
        if(maybeOrder.isEmpty()){
            ordersMenu.showError("Книга не найдена");
        }
        else{
            ordersMenu.showOrder(maybeOrder.get());
        }
    }

    @Override
    public void setOrderStatus() {
        Order order = getOrderFromConsole();

        OrderStatus newStatus = getStatusFromConsole();

        mainManager.setOrderStatus(order, newStatus);
    }

    @Override
    public OrderStatus getStatusFromConsole(){
        ordersMenu.showGetNewStatus();
        String new_status = scanner.nextLine();

        while(!Objects.equals(new_status, OrderStatus.COMPLETED.toString())
                && !Objects.equals(new_status, OrderStatus.NOT_COMPLETED.toString())) {
            ordersMenu.showErrorInputStatus();
            new_status = scanner.nextLine();
        }

        return OrderStatus.valueOf(new_status);
    }

    @Override
    public Order getOrderFromConsole(){
        ordersMenu.showGetClientName();
        String clientName = scanner.nextLine();

        Book book = getBookFromConsole(ordersMenu);

        return new Order(book, clientName);
    }

    @Override
    public void getOrdersByDate() { ordersMenu.showOrders(mainManager.getOrdersByDate()); }

    @Override
    public void getOrdersByPrice() { ordersMenu.showOrders(mainManager.getOrdersByPrice()); }

    @Override
    public void getOrdersByStatus() { ordersMenu.showOrders(mainManager.getOrdersByStatus()); }

    @Override
    public void getCountCompletedOrders(){
        ordersMenu.showGetBeginDate();
        LocalDate begin = getDateFromConsole();

        ordersMenu.showGetEndDate();
        LocalDate end = getDateFromConsole();

        ordersMenu.showCountCompletedOrders(mainManager.getCountCompletedOrders(begin, end));
    }

    @Override
    public void getEarnedSum(){
        ordersMenu.showGetBeginDate();
        LocalDate begin = getDateFromConsole();

        ordersMenu.showGetEndDate();
        LocalDate end = getDateFromConsole();

        ordersMenu.showEarnedSum(mainManager.getEarnedSum(begin, end));
    }

    @Override
    public void getCompletedOrdersByDate() {
        ordersMenu.showGetBeginDate();
        LocalDate begin = getDateFromConsole();

        ordersMenu.showGetEndDate();
        LocalDate end = getDateFromConsole();

        ordersMenu.showOrders(mainManager.getCompletedOrdersByDate(begin, end));
    }

    @Override
    public void getCompletedOrdersByPrice() {
        ordersMenu.showGetBeginDate();
        LocalDate begin = getDateFromConsole();

        ordersMenu.showGetEndDate();
        LocalDate end = getDateFromConsole();

        ordersMenu.showOrders(mainManager.getCompletedOrdersByPrice(begin, end));
    }

    @Override
    public LocalDate getDateFromConsole(){
        ordersMenu.showGetYear();
        int year = scanner.nextInt();

        ordersMenu.showGetMonth();
        int month = scanner.nextInt();

        ordersMenu.showGetDay();
        int day = scanner.nextInt();

        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            ordersMenu.showErrorInputDate();
            return getDateFromConsole();
        }
    }
}