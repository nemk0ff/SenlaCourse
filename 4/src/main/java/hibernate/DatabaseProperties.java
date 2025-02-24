package hibernate;

import annotations.ConfigProperty;
import config.ConfigurationManager;

public class DatabaseProperties {
  @ConfigProperty(propertyName = "db.url")
  private String databaseUrl;
  @ConfigProperty(propertyName = "db.user")
  private String databaseUser;
  @ConfigProperty(propertyName = "db.password")
  private String databasePassword;

  public DatabaseProperties() {
    ConfigurationManager.configure(this);
  }

  public String getDatabaseUrl() {
    return databaseUrl;
  }

  public String getDatabaseUser() {
    return databaseUser;
  }

  public String getDatabasePassword() {
    return databasePassword;
  }
}
