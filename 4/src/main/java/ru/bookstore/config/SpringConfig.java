package ru.bookstore.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan("ru.bookstore")
@EnableWebMvc
@PropertySource("classpath:config.properties")
@EnableTransactionManagement
public class SpringConfig implements WebMvcConfigurer {
  @Value("${db.url}")
  private String databaseUrl;
  @Value("${db.user}")
  private String databaseUser;
  @Value("${db.password}")
  private String databasePassword;

  @Bean
  public DataSource dataSource() {
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setJdbcUrl(databaseUrl);
    dataSource.setUsername(databaseUser);
    dataSource.setPassword(databasePassword);
    dataSource.setMaximumPoolSize(10);
    dataSource.setMinimumIdle(2);
    dataSource.setIdleTimeout(30000);
    return dataSource;
  }

  @Bean
  public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);
    sessionFactory.setPackagesToScan("ru");
    sessionFactory.setHibernateProperties(getProperties());
    return sessionFactory;
  }

  @Bean
  public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
    HibernateTransactionManager transactionManager = new HibernateTransactionManager();
    transactionManager.setSessionFactory(sessionFactory);
    return transactionManager;
  }

  private Properties getProperties() {
    Properties properties = new Properties();
    properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
    properties.setProperty("hibernate.show_sql", "true");
    properties.setProperty("hibernate.format_sql", "true");
    properties.setProperty("hibernate.hbm2ddl.auto", "update");
    return properties;
  }
}

