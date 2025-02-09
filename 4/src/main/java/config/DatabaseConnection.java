package config;

import annotations.ConfigProperty;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.Data;

/**
 * {@code DatabaseConnection} - Класс, управляющий подключением к базе данных.
 * Обеспечивает настройку соединения на основе значений из конфигурационного файла.
 */
@Data
public class DatabaseConnection {
  @ConfigProperty(propertyName = "db.url")
  private String databaseUrl;
  @ConfigProperty(propertyName = "db.user")
  private String databaseUser;
  @ConfigProperty(propertyName = "db.password")
  private String databasePassword;

  private final Connection connection;

  /**
   * Создает новое подключение к базе данных на основе конфигурации.
   *
   * @throws SQLException Если не удалось установить соединение с базой данных.
   */
  public DatabaseConnection() throws SQLException {
    ConfigurationManager.configure(this);
    this.connection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
    this.connection.setAutoCommit(false);
  }

  /**
   * Возвращает текущее соединение с базой данных.
   *
   * @return Объект {@link Connection}, представляющий соединение с базой данных.
   */
  public Connection connection() {
    return connection;
  }
}