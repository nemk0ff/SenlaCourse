package config;

import controllers.BooksController;
import controllers.OrdersController;
import controllers.RequestsController;
import controllers.impl.BooksControllerImpl;
import controllers.impl.MainController;
import controllers.impl.OrdersControllerImpl;
import controllers.impl.RequestsControllerImpl;
import dao.BookDao;
import dao.OrderDao;
import dao.RequestDao;
import dao.impl.BookDaoImpl;
import dao.impl.OrderDaoImpl;
import dao.impl.RequestDaoImpl;
import hibernate.HibernateUtil;
import manager.MainManager;
import manager.MainManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import view.BooksMenu;
import view.OrdersMenu;
import view.RequestsMenu;
import view.impl.BooksMenuImpl;
import view.impl.MainMenu;
import view.impl.OrdersMenuImpl;
import view.impl.RequestsMenuImpl;

@Configuration
@ComponentScan("java")
@PropertySource("classpath:config.properties")
public class SpringConfig {
  @Bean
  public MainController mainController() {
    return new MainController(mainManager(), mainMenu(), booksController(),
        ordersController(), requestsController());
  }

  @Bean
  public BooksMenu booksMenu() {
    return new BooksMenuImpl();
  }

  @Bean
  public OrdersMenu ordersMenu() {
    return new OrdersMenuImpl();
  }

  @Bean
  public RequestsMenu requestsMenu() {
    return new RequestsMenuImpl();
  }

  @Bean
  public MainMenu mainMenu() {
    return new MainMenu();
  }

  @Bean
  public BooksController booksController() {
    return new BooksControllerImpl(mainManager(), booksMenu());
  }

  @Bean
  public OrdersController ordersController() {
    return new OrdersControllerImpl(mainManager(), ordersMenu());
  }

  @Bean
  public RequestsController requestsController() {
    return new RequestsControllerImpl(mainManager(), requestsMenu());
  }

  @Bean
  public MainManager mainManager() {
    return new MainManagerImpl(hibernateUtil(), bookDao(), orderDao(), requestDao());
  }

  @Bean
  public HibernateUtil hibernateUtil() {
    return new HibernateUtil();
  }

  @Bean
  public BookDao bookDao() {
    return new BookDaoImpl();
  }

  @Bean
  public OrderDao orderDao() {
    return new OrderDaoImpl();
  }

  @Bean
  public RequestDao requestDao() {
    return new RequestDaoImpl();
  }
}
