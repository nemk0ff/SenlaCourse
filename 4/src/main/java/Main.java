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
import managers.MainManager;
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

        di.registerBean(DI.class, () -> di);

        di.registerBean(ObjectMapper.class, () -> {
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            return objectMapper;
        });

        di.registerBean(DeserializationManager.class, new DeserializationManager());

        di.registerBean(MainManager.class, () -> {
            DeserializationManager deserializer = di.getBean(DeserializationManager.class);
            return deserializer.deserialization();
        });

        di.registerBean(MainController.class, new MainController());

        di.registerBean(MainMenu.class, new MainMenu());

        di.registerBean(BooksMenu.class, new BooksMenuImpl());

        di.registerBean(OrdersMenu.class, new OrdersMenuImpl());

        di.registerBean(RequestsMenu.class, new RequestsMenuImpl());

        di.registerBean(BooksControllerImpl.class, new BooksControllerImpl());

        di.registerBean(OrdersControllerImpl.class,
                OrdersControllerImpl::new);

        di.registerBean(RequestsControllerImpl.class,
                RequestsControllerImpl::new);

        di.registerBean(SerializationManager.class, SerializationManager::new);

        MainController mainController = di.getBean(MainController.class);
        mainController.run();
    }
    // При десериализации устанавливать счётчик заказов и запросов на (макс. id + 1)
    // При изменении статуса с NEW на COMPLETED нужно устанавливать дату завершения
}
