package application.Intergration;

import application.Controllers.SignUpController;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SignUpControllerIntegrationTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private SignUpController signUpController;

    @BeforeEach
    public void setUp() throws SQLException, InterruptedException {
        MockitoAnnotations.openMocks(this);

        new JFXPanel(); // Initializes JavaFX environment

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            // Initialize controller and fields
            signUpController = new SignUpController();
            initializeTextFields();
            signUpController.connection = mockConnection;

            // Mock the behavior of the prepared statement
            try {
                when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
                when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            latch.countDown(); // Signal that initialization is complete
        });

        latch.await(5, TimeUnit.SECONDS); // Wait for JavaFX initialization
    }

    private void initializeTextFields() {
        signUpController.idField = new TextField();
        signUpController.nameField = new TextField();
        signUpController.surnameField = new TextField();
        signUpController.usernameField = new TextField();
        signUpController.passwordField = new PasswordField();
    }

    @Test
    public void testSignUpSuccess() throws SQLException {
        Platform.runLater(() -> {
            setUpTextFields("1", "John", "Doe", "johndoe", "password");

            // Mock the behavior of the prepared statement
            try {
                when(mockPreparedStatement.executeUpdate()).thenReturn(1);
                when(mockResultSet.next()).thenReturn(false); // Simulate no existing user or id
            } catch (SQLException e) {
                e.printStackTrace();
            }

            signUpController.signUp(null);

            try {
                verify(mockPreparedStatement, times(1)).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void testSignUpFailure() throws SQLException {
        Platform.runLater(() -> {
            setUpTextFields("1", "John", "Doe", "johndoe", "password");

            // Mock the behavior of the prepared statement
            try {
                when(mockPreparedStatement.executeUpdate()).thenReturn(0);
                when(mockResultSet.next()).thenReturn(false); // Simulate no existing user or id
            } catch (SQLException e) {
                e.printStackTrace();
            }

            signUpController.signUp(null);

            try {
                verify(mockPreparedStatement, times(1)).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setUpTextFields(String id, String name, String surname, String username, String password) {
        signUpController.idField.setText(id);
        signUpController.nameField.setText(name);
        signUpController.surnameField.setText(surname);
        signUpController.usernameField.setText(username);
        signUpController.passwordField.setText(password);
    }
}
