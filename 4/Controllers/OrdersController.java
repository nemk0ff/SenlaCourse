package Controllers;

import Model.Book;
import Model.OrderStatus;

import java.time.LocalDate;
import java.util.List;

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

    List<Book> getBooksFromConsole();
}
