import config.SpringConfig;
import controllers.impl.MainController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * {@code Main} - Главный класс приложения, отвечающий за запуск приложения, инициализацию
 * контейнера зависимостей и передачу управления главному контроллеру.
 */
@Slf4j
public class Main {

  /**
   * Главный метод приложения, точка входа.
   *
   * <p>Этот метод выполняет следующие действия:</p>
   * <ol>
   *   <li>Создает экземпляр контейнера зависимостей ({@link SpringConfig}).</li>
   *   <li>Регистрирует главный контроллер ({@link MainController}) в контейнере
   *   зависимостей.</li>
   *   <li>Получает экземпляр главного контроллера из контейнера зависимостей.</li>
   *   <li>Запускает главный контроллер, передавая ему управление приложением.</li>
   * </ol>
   */
  public static void main(String[] args) {
    try (AnnotationConfigApplicationContext context =
             new AnnotationConfigApplicationContext(SpringConfig.class)) {
      MainController mainController = context.getBean("mainController", MainController.class);
      mainController.run();
    } catch (Exception e) {
      log.error("Завершение приложения из-за необработанного исключения: {}", e.getMessage(), e);
      System.exit(1);
    }
  }
}
