package Controllers;

import Model.Order;
import Model.OrderStatus;

import java.time.LocalDate;

public interface OrdersController extends Controller {
    Order getOrderFromConsole();

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
}
