import DI.DI;

import controllers.impl.MainController;

public class Main {
    public static void main(String[] args) {
        DI di = DI.getInstance();

        di.registerBean(MainController.class, new MainController());

        MainController mainController = di.getBean(MainController.class);
        mainController.run();
    }
}
