package DI;

import annotations.DIComponentDependency;

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
        injectDependencies(bean);
        return (T) bean;
    }

    private void injectDependencies(Object bean) {
        Class<?> beanClass = bean.getClass();

        while (beanClass != null) {
            for (Field field : beanClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(DIComponentDependency.class)) {
                    Object dependency = getBean(field.getType());
                    if (dependency != null) {
                        try {
                            field.setAccessible(true);
                            field.set(bean, dependency);
                        } catch (IllegalAccessException e) {
                            System.err.println("Can't inject field: " + field.getName()
                                    + "; message: " + e.getMessage());
                        }
                    } else {
                        System.err.println("Can't find dependency: " + field.getType().getName() + " for " + beanClass.getName());
                    }
                }
            }
            // Проходим по всей иерархии классов нашего объекта
            // Если дойдём до Object, то object.getSuperclass() вернёт null
            beanClass = beanClass.getSuperclass();
        }
    }
}