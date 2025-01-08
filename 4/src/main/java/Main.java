import DI.DI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import config.DeserializationManager;
import config.SerializationManager;
import controllers.impl.BooksControllerImpl;
import controllers.impl.MainController;
import controllers.impl.OrdersControllerImpl;
import controllers.impl.RequestsControllerImpl;
import managers.LibraryManager;
import managers.MainManager;
import managers.OrdersManager;
import managers.impl.LibraryManagerImpl;
import managers.impl.MainManagerImpl;
import managers.impl.OrdersManagerImpl;
import view.BooksMenu;
import view.OrdersMenu;
import view.RequestsMenu;
import view.impl.BooksMenuImpl;
import view.impl.MainMenu;
import view.impl.OrdersMenuImpl;
import view.impl.RequestsMenuImpl;


public class Main {
    public static void main(String[] args) {
        DI di = DI.getInstance();

        di.registerBean(MainController.class, new MainController());

        MainController mainController = di.getBean(MainController.class);
        mainController.run();
    }
}
