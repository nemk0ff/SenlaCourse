package hibernate;

import config.ConfigurationManager;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import model.impl.Book;
import model.impl.Order;
import model.impl.Request;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

@Slf4j
public class HibernateUtil {
  private static final DatabaseProperties databaseProperties = new DatabaseProperties();
  private static Session session;
  private static SessionFactory sessionFactory;

  /**
   * Инициализирует SessionFactory.
   */
  public HibernateUtil() {
    ConfigurationManager.configure(this);
  }

  private static void initializeSessionFactory() {
    if (sessionFactory == null) {
      try {
        Properties settings = getProperties();

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
            .applySettings(settings)
            .build();

        sessionFactory = new MetadataSources(serviceRegistry)
            .addAnnotatedClass(Book.class)
            .addAnnotatedClass(Order.class)
            .addAnnotatedClass(Request.class)
            .buildMetadata()
            .buildSessionFactory();
      } catch (Exception e) {
        log.error("Ошибка инициализации SessionFactory: {}", e.getMessage());
      }
    }
  }

  private static Properties getProperties() {
    Properties settings = new Properties();
    settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
    settings.put(Environment.URL, databaseProperties.getDatabaseUrl());
    settings.put(Environment.USER, databaseProperties.getDatabaseUser());
    settings.put(Environment.PASS, databaseProperties.getDatabasePassword());

    settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
    settings.put(Environment.SHOW_SQL, true);
    settings.put(Environment.FORMAT_SQL, true);
    settings.put(Environment.HBM2DDL_AUTO, "update");
    return settings;
  }

  /**
   * Возвращает новую сессию.
   *
   * @return Новая сессия.
   */
  public static Session getSession() {
    if (sessionFactory == null) {
      initializeSessionFactory();
    }
    if (session != null && session.isOpen()) {
      return session;
    }
    session = sessionFactory.openSession();
    return session;
  }
}

