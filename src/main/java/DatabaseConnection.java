import java.sql.*;
import java.util.Properties;
import java.util.Optional;
import java.util.function.Function;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnection {
    private static final String CONFIG_FILE = "application.properties";
    
    public static Optional<Properties> loadDatabaseProperties() {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                return Optional.empty();
            }
            Properties props = new Properties();
            props.load(input);
            return Optional.of(props);
        } catch (IOException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public static Optional<Connection> createConnection() {
        return loadDatabaseProperties()
            .map(props -> {
                try {
                    String url = props.getProperty("db.url", "jdbc:postgresql://localhost:5432/academic_events_db");
                    String username = props.getProperty("db.username", "postgres");
                    String password = props.getProperty("db.password", "password");
                    
                    return DriverManager.getConnection(url, username, password);
                } catch (SQLException e) {
                    System.err.println("Database connection error: " + e.getMessage());
                    return null;
                }
            })
            .filter(conn -> conn != null);
    }
    
    @FunctionalInterface
    public interface DatabaseOperation<T> {
        T execute(Connection conn) throws SQLException;
    }
    
    public static <T> Optional<T> executeWithConnection(DatabaseOperation<T> operation) {
        return createConnection()
            .map(conn -> {
                try (Connection connection = conn) {
                    return operation.execute(connection);
                } catch (SQLException e) {
                    System.err.println("Database operation error: " + e.getMessage());
                    return null;
                }
            })
            .filter(result -> result != null);
    }
}
