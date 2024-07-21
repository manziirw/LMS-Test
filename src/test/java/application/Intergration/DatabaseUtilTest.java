package application.Intergration;

import application.Utils.DatabaseUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DatabaseUtilTest {

    @Test
    public void testGetConnectionSuccess() throws SQLException {
        // Act
        Connection connection = DatabaseUtil.getConnection();

        // Assert
        assertNotNull(connection, "Connection should not be null");

        // Optionally, test if the connection can interact with the database
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SELECT 1");
        }
    }

    @Test
    public void testCloseConnection() throws SQLException {
        // Arrange
        Connection connection = DatabaseUtil.getConnection();

        // Act
        DatabaseUtil.close(connection);

        // Assert
        // No additional assertion needed for the close method itself
    }

    @Test
    public void testCloseConnectionException() throws SQLException {
        // Arrange
        Connection mockConnection = org.mockito.Mockito.mock(Connection.class);
        org.mockito.Mockito.doThrow(new SQLException("Close failed")).when(mockConnection).close();

        // Act
        DatabaseUtil.close(mockConnection);

        // Assert
        // No additional assertion needed as the focus is on handling exceptions
    }
}
