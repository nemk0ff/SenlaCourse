import Controllers.Impl.MainController;

public class Main {
    public static void main(String[] args) {
        MainController myController = new MainController();
        myController.run();
    }

    // TODO: добавить к Request поле amount. Если в заказе не хватает 5 книг "Война и мир",
    //  то нужен один запрос на 5 таких книг (потребуется изменить mainManager.createRequest и mainManager.closeRequests)
}