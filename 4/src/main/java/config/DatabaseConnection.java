package config;

import annotations.ConfigProperty;
import lombok.Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Data
public class DatabaseConnection {
    @ConfigProperty(propertyName = "db.url")
    private String databaseUrl;
    @ConfigProperty(propertyName = "db.user")
    private String databaseUser;
    @ConfigProperty(propertyName = "db.password")
    private String databasePassword;

    private final Connection connection;

    public DatabaseConnection() throws SQLException {
        ConfigurationManager.configure(this);
        this.connection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
        this.connection.setAutoCommit(false);
    }

    public Connection connection() {
        return connection;
    }
}
