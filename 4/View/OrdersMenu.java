package View;

import Model.Order;

import java.util.List;

public interface OrdersMenu extends Menu{
    void showOrders(List<Order> orders);
    void showOrder(Order order);

    void showGetBeginDate();
    void showGetEndDate();

    void showGetYear();
    void showGetMonth();
    void showGetDay();
    void showErrorInputDate();

    void showGetClientName();

    void showGetNewStatus();
    void showErrorInputStatus();

    void showCountCompletedOrders(Long count);
    void showEarnedSum(Double sum);
}
