package controllers;

import model.OrderStatus;

import java.time.LocalDate;
import java.util.Map;

public interface OrdersController extends Controller {
    LocalDate getDateFromConsole();

    OrderStatus getStatusFromConsole();

    void createOrder();

    void cancelOrder();

    void showOrderDetails();

    void setOrderStatus();

    void getOrdersByDate();

    void getOrdersByPrice();

    void getOrdersByStatus();

    void getCompletedOrdersByDate();

    void getCompletedOrdersByPrice();

    void getCountCompletedOrders();

    void getEarnedSum();

    LocalDate getBeginDate();

    LocalDate getEndDate();

    String getClientNameFromConsole();

    Map<Long, Integer> getBooksFromConsole();

    long getBookFromConsole(int index);

    void importAll();

    void importOrder();

    void exportOrder();
}
