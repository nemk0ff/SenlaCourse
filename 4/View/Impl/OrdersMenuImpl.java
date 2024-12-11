package View.Impl;

import Model.Book;
import Model.Order;
import View.OrdersMenu;

import java.util.List;

public class OrdersMenuImpl implements OrdersMenu {

    @Override
    public void showMenu() {
        System.out.println("##############################");
        System.out.println("########  МЕНЮ ЗАКАЗОВ  ######");
        System.out.println("##############################");
        System.out.println("    Выберите действие:");
        System.out.println("1. Создать заказ");
        System.out.println("2. Отменить заказ");
        System.out.println("3. Посмотреть детали заказа");
        System.out.println("4. Изменить статус заказа");
        System.out.println("5. Вывести список заказов (сортировка по дате исполнения)");
        System.out.println("6. Вывести список заказов (сортировка по цене)");
        System.out.println("7. Вывести список заказов (сортировка по статусу)");
        System.out.println("8. Вывести список выполненных заказов за период времени(сортировка по дате)");
        System.out.println("9. Вывести список выполненных заказов за период времени(сортировка по цене)");
        System.out.println("10. Вывести количество выполненных заказов за период времени");
        System.out.println("11. Вывести сумму заработанных средств за период времени");
        System.out.println("12. Вернуться в главное меню");
        System.out.println("13. Выйти из программы");
    }

    @Override
    public void showOrders(List<Order> orders) {
        if(orders.isEmpty()){
            System.out.println("У магазина пока не было заказов");
            return;
        }

        System.out.println("============ ЗАКАЗЫ ============");
        orders.forEach(this::showOrder);
        System.out.println("================================");
    }

    @Override
    public void showOrder(Order order) {
        System.out.println(order.getInfoAbout());
        for (Book book: order.getBooks()) {
            System.out.println(book.getInfoAbout());
        }
        System.out.println();
    }

    @Override
    public void showGetBeginDate() {
        System.out.println("Введите дату начала периода");
    }

    @Override
    public void showGetEndDate() {
        System.out.println("Введите дату конца периода");
    }

    @Override
    public void showGetYear() {
        System.out.print("Введите год: ");
    }

    @Override
    public void showGetMonth() {
        System.out.print("Введите месяц: ");
    }

    @Override
    public void showGetDay() {
        System.out.print("Введите день: ");
    }

    @Override
    public void showGetClientName() {
        System.out.print("Введите имя клиента: ");
    }

    @Override
    public void showGetBook(int index){
        System.out.println("Введите книгу №" + index);
    }

    @Override
    public void showGetNewStatus() {
        System.out.print("Введите новый статус заказа (NEW, COMPLETED или CANCELED): ");
    }

    @Override
    public void showCountCompletedOrders(Long count) {
        System.out.println("Количество выполненных заказов : " + count);
    }

    @Override
    public void showEarnedSum(Double sum) {
        System.out.println("Сумма заработанных средств : " + sum);
    }
}
