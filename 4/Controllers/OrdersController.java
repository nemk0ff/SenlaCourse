package Controllers;

import Model.Items.OrderStatus;

import java.time.LocalDate;
import java.util.Map;

public interface OrdersController extends Controller {
    String importPath = "4/Import/importOrders.csv";
    String exportPath = "4/Export/exportOrders.csv";

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

    void exportOrder();

    void importAll();
}
