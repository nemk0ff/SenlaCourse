package DI;

import annotations.DIComponentDependency;
import config.DeserializationManager;
import controllers.impl.MainController;
import managers.MainManager;
import managers.impl.MainManagerImpl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DI {
    private static DI instance;
    private final Map<Class<?>, Object> beans = new HashMap<>();

    private DI() {
    }

    public static DI getInstance() {
        if (instance == null) {
            instance = new DI();
        }
        return instance;
    }

    public <T> void registerBean(Class<T> clazz, Object bean) {
        beans.put(clazz, bean);
    }

    public <T> void registerBean(Class<T> clazz, Supplier<T> beanSupplier) {
        beans.put(clazz, beanSupplier);
    }

    public <T> T getBean(Class<T> objClass) {
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

    private void injectDependenciesRecursively(Object bean) {
        if (bean == null) {
            return;
        }
        Class<?> beanClass = bean.getClass();

        if (!beans.containsKey(beanClass)) {
            registerBean(beanClass, bean);
        }

        while (beanClass != null) {
            for (Field field : beanClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(DIComponentDependency.class)) {
                    Object dependency = getBean(field.getType());
                    if (dependency == null) {
                        // Если зависимость не найдена, пытаемся создать новый экземпляр
                        try {
                            dependency = field.getType().getDeclaredConstructor().newInstance();
                            registerBean(field.getType(), dependency);
                        } catch (Exception e) {
                            System.err.println("Can't create instance of class: " + field.getType().getName()
                                    + "; message: " + e.getMessage());
                            continue;
                        }
                    }
                    // Если нашли поле с аннотацией, то тоже делаем Inject
                    try {
                        field.setAccessible(true);
                        field.set(bean, dependency);
                        injectDependenciesRecursively(dependency);
                    } catch (IllegalAccessException e) {
                        System.err.println("Can't inject field: " + field.getName()
                                + "; message: " + e.getMessage());
                    }
                }
            }
            beanClass = beanClass.getSuperclass();
        }
    }
}