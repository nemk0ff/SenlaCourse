package hibernate;

import java.util.Properties;
import lombok.RequiredArgsConstructor;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class HibernateUtil {
  @Value("${db.url}")
  private String databaseUrl;
  @Value("${db.user}")
  private String databaseUser;
  @Value("${db.password}")
  private String databasePassword;
  private Session session;
  private SessionFactory sessionFactory;

  private void initializeSessionFactory() {
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

  private Properties getProperties() {
    Properties settings = new Properties();
    settings.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
    settings.put("jakarta.persistence.jdbc.url", databaseUrl);
    settings.put("jakarta.persistence.jdbc.user", databaseUser);
    settings.put("jakarta.persistence.jdbc.password", databasePassword);

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
  public Session getSession() {
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

