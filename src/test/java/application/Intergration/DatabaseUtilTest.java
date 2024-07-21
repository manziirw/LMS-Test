package application.Intergration;

import application.Utils.DatabaseUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DatabaseUtilTest {

    @Test
    public void testGetConnectionSuccess() throws Exception {
        // Arrange
        // Mock DriverManager.getConnection method
        Connection mockConnection = mock(Connection.class);
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            // Act
            Connection connection = DatabaseUtil.getConnection();

            // Assert
            assertNotNull(connection, "Connection should not be null");
            verify(mockedDriverManager, times(1))
                    .when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()));
        }
    }

    @Test
    public void testGetConnectionDriverNotFound() throws Exception {
        // Arrange
        try (MockedStatic<Class> mockedClass = mockStatic(Class.class)) {
            mockedClass.when(() -> Class.forName("com.mysql.cj.jdbc.Driver"))
                    .thenThrow(new ClassNotFoundException());

            // Act & Assert
            assertThrows(SQLException.class, DatabaseUtil::getConnection,
                    "SQLException should be thrown when JDBC driver is not found");
        }
    }

    @Test
    public void testCloseConnection() throws SQLException {
        // Arrange
        Connection mockConnection = mock(Connection.class);

        // Act
        DatabaseUtil.close(mockConnection);

        // Assert
        verify(mockConnection, times(1)).close();
    }

    @Test
    public void testCloseConnectionException() throws SQLException {
        // Arrange
        Connection mockConnection = mock(Connection.class);
        doThrow(new SQLException("Close failed")).when(mockConnection).close();

        // Act
        DatabaseUtil.close(mockConnection);

        // Assert
        verify(mockConnection, times(1)).close();
    }
}

