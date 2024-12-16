import Controllers.Impl.MainController;

public class Main {
    public static void main(String[] args) {
        MainController myController = new MainController();
        myController.run();
    }

    // TODO: поменять List на TreeMap, чтобы ускорить поиск сущностей в магазине
    // TODO: добавить к Request поле amount. Если в заказе не хватает 5 книг "Война и мир",
    //  то нужен один запрос на 5 таких книг (потребуется изменить mainManager.createRequest и mainManager.closeRequests)
    // TODO: понять, что делать при импорте запросов. Если у нас был запрос №1 на книгу "Детство", и мы импортировали
    //  запрос №1 на книгу "Юность", то произойдет перезапись запроса №1. При этом когда будет закрываться заказ на
    //  книгу "Детство" произойдет попытка закрыть запрос на эту книгу. Но запроса на эту книгу нет...
}