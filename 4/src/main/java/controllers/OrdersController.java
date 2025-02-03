package controllers;

import model.OrderStatus;

import java.time.LocalDate;
import java.util.Map;

public interface OrdersController extends Controller {
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

    void importAll();

    void exportAll();

    void importOrder();

    void exportOrder();
}
