package di;

import annotations.ComponentDependency;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * {@code DiContainer} - Класс, реализующий контейнер внедрения зависимостей (Dependency
 * Injection Container).  Использует паттерн Singleton для обеспечения единственного экземпляра.
 * Позволяет регистрировать и получать бины (beans) и автоматически внедрять зависимости,
 * отмеченные аннотацией {@link ComponentDependency}.
 */
public class DiContainer {
  private static DiContainer instance;
  private final Map<Class<?>, Object> beans = new HashMap<>();

  private DiContainer() {
  }

  /**
   * Возвращает единственный экземпляр контейнера внедрения зависимостей.
   * Если экземпляр еще не создан, он будет создан при первом вызове этого метода.
   */
  public static DiContainer getInstance() {
    if (instance == null) {
      instance = new DiContainer();
    }
    return instance;
  }

  /**
   * Регистрирует бин в контейнере.
   */
  public <T> void registerBean(Class<T> clazz, Object bean) {
    beans.put(clazz, bean);
  }

  /**
   * Возвращает бин из контейнера по его типу класса.
   * Если бин не найден, возвращает {@code null}.  Если бин является Supplier,
   * вызывает метод `get()` для получения объекта.  Автоматически внедряет зависимости
   * в полученный бин.
   */
  public <T> T getBean(Class<T> objClass) throws Exception {
    Object bean = beans.get(objClass);
    if (bean == null) {
      return null;
    }
    if (bean instanceof Supplier) {
      bean = ((Supplier<?>) bean).get();
    }
    injectDependenciesRecursively(bean);
    return (T) bean;
  }

  private void injectDependenciesRecursively(Object bean) throws Exception {
    if (bean == null) {
      return;
    }
    Class<?> beanClass = bean.getClass();

    if (!beans.containsKey(beanClass)) {
      registerBean(beanClass, bean);
    }

    while (beanClass != null) {
      for (Field field : beanClass.getDeclaredFields()) {
        if (field.isAnnotationPresent(ComponentDependency.class)) {
          Object dependency = getBean(field.getType());
          if (dependency == null) {
            try {
              dependency = field.getType().getDeclaredConstructor().newInstance();
              registerBean(field.getType(), dependency);
            } catch (Exception e) {
              throw new Exception("Can't create instance of class: " + field.getType().getName()
                  + "; message: " + e.getMessage());
            }
          }
          try {
            field.setAccessible(true);
            field.set(bean, dependency);
            injectDependenciesRecursively(dependency);
          } catch (IllegalAccessException e) {
            throw new Exception("Can't inject field: " + field.getName()
                + "; message: " + e.getMessage());
          }
        }
      }
      beanClass = beanClass.getSuperclass();
    }
  }
}