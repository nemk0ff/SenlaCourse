package config;

import annotations.ConfigProperty;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code ConfigurationManager} - Класс для автоматической настройки полей объекта
 * с использованием значений из конфигурационных файлов.
 */
@Slf4j
public class ConfigurationManager {

  /**
   * Настраивает поля объекта, помеченные аннотацией {@link ConfigProperty},
   * загружая значения из указанных конфигурационных файлов.
   *
   * @param target Объект, поля которого необходимо настроить.
   */
  public static void configure(Object target) {
    Class<?> clazz = target.getClass();
    log.debug("Конфигурация объекта класса: {}...", clazz.getName());

    for (Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(ConfigProperty.class)) {
        log.debug("Обнаружено поле с аннотацией ConfigProperty: {}", field.getName());
        ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
        String configFileName = annotation.configFileName();
        String propertyName = annotation.propertyName();
        Class<?> targetType = annotation.type();

        if (propertyName.isEmpty()) {
          propertyName = clazz.getSimpleName() + "." + field.getName();
        }
        log.debug("Используемое имя свойства: {}", propertyName);

        try {
          Properties properties = loadProperties(configFileName);
          String propertyValue = properties.getProperty(propertyName);

          if (propertyValue != null) {
            field.setAccessible(true);
            Object convertedValue = convertValue(propertyValue, targetType);
            field.set(target, convertedValue);
            log.debug("В поле {} успешно заинжекчено значение: {}",
                field.getName(), convertedValue);
          } else {
            log.warn("Свойство {} не найдено в файле конфигурации {}",
                propertyName, configFileName);
          }
        } catch (IOException | IllegalAccessException e) {
          String errorMessage = "Ошибка при конфигурировании поля: "
              + field.getName() + " : " + e.getMessage();
          log.error(errorMessage, e);
          throw new RuntimeException(errorMessage);
        }
      } else {
        log.debug("Поле {} не имеет аннотации ConfigProperty, пропускаем", field.getName());
      }
    }
    log.debug("Конфигурирование объекта класса {} завершено.", clazz.getName());
  }


  private static Properties loadProperties(String configFileName) throws IOException {
    Properties properties = new Properties();
    try (InputStream input = ConfigurationManager.class.getClassLoader()
        .getResourceAsStream(configFileName)) {
      if (input == null) {
        throw new IOException("Файл конфигурирования не найден: " + configFileName);
      }
      properties.load(input);
    }
    return properties;
  }

  private static Object convertValue(String value, Class<?> targetType) {
    if (targetType == String.class) {
      return value;
    } else if (targetType == int.class || targetType == Integer.class) {
      return Integer.parseInt(value);
    } else if (targetType == double.class || targetType == Double.class) {
      return Double.parseDouble(value);
    } else if (targetType == boolean.class || targetType == Boolean.class) {
      return Boolean.parseBoolean(value);
    }

    return value;
  }
}