import DI.DI;

import controllers.impl.MainController;
import lombok.SneakyThrows;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        DI di = DI.getInstance();

        di.registerBean(MainController.class, new MainController());

        MainController mainController = di.getBean(MainController.class);
        mainController.run();
    }
}
