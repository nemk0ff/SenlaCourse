package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code ConfigProperty} - Аннотация, используемая для внедрения значений конфигурации
 * в поля класса.
 *
 * <p>Эта аннотация позволяет указать, из какого конфигурационного файла и какое свойство
 * следует использовать для инициализации поля.</p>
 *
 * <p>Пример использования:</p>
 * <pre>
 * {@code
 * public class MyService {
 *   @ConfigProperty(configFileName = "app.properties", propertyName = "database.url")
 *   private String databaseUrl;
 * }
 * }
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {
  /**
   * Указывает имя конфигурационного файла, из которого будет загружено свойство.
   *
   * @return Имя конфигурационного файла. По умолчанию "config.properties".
   */
  String configFileName() default "config.properties";

  /**
   * Указывает имя свойства в конфигурационном файле.
   *
   * @return Имя свойства. Если не указано, используется имя поля, к которому применена аннотация.
   */
  String propertyName() default "";

  /**
   * Указывает тип данных, в который необходимо преобразовать значение свойства.
   *
   * @return Класс типа данных. По умолчанию {@link String}.
   */
  Class<?> type() default String.class;
}