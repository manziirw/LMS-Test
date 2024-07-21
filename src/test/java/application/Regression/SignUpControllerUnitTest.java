package application.Regression;

import application.Controllers.SignUpController;
import javafx.embed.swing.JFXPanel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SignUpControllerUnitTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private SignUpController signUpController;

    @BeforeEach
    public void setUp() throws SQLException {
        // Initialize JavaFX runtime
        new JFXPanel();  // This is needed to initialize JavaFX toolkit

        // Ensure that all JavaFX related code is run on the FX Application Thread
        Platform.runLater(() -> {
            MockitoAnnotations.openMocks(this);
            try {
                when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            signUpController.idField = new TextField();
            signUpController.nameField = new TextField();
            signUpController.surnameField = new TextField();
            signUpController.usernameField = new TextField();
            signUpController.passwordField = new PasswordField();
        });
    }

    @Test
    public void testSignUpSuccess() throws SQLException {
        Platform.runLater(() -> {
            // Arrange
            signUpController.idField.setText("1");
            signUpController.nameField.setText("John");
            signUpController.surnameField.setText("Doe");
            signUpController.usernameField.setText("john_doe");
            signUpController.passwordField.setText("password123");

            try {
                when(preparedStatement.executeQuery()).thenReturn(resultSet);
                when(resultSet.next()).thenReturn(false); // Username and ID do not exist
                when(preparedStatement.executeUpdate()).thenReturn(1); // Insertion is successful
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Act
            signUpController.signUp(new ActionEvent());

            // Assert
            try {
                verify(preparedStatement, times(1)).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // Additional assertions can be added to verify alert messages and navigation
        });
    }

    @Test
    public void testSignUpUsernameExists() throws SQLException {
        Platform.runLater(() -> {
            // Arrange
            signUpController.idField.setText("1");
            signUpController.nameField.setText("John");
            signUpController.surnameField.setText("Doe");
            signUpController.usernameField.setText("existing_user");
            signUpController.passwordField.setText("password123");

            try {
                when(preparedStatement.executeQuery()).thenReturn(resultSet);
                when(resultSet.next()).thenReturn(true); // Username exists
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Act
            signUpController.signUp(new ActionEvent());

            // Assert
            try {
                verify(preparedStatement, never()).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // Additional assertions can be added to verify alert messages
        });
    }

    @Test
    public void testSignUpIdExists() throws SQLException {
        Platform.runLater(() -> {
            // Arrange
            signUpController.idField.setText("1");
            signUpController.nameField.setText("John");
            signUpController.surnameField.setText("Doe");
            signUpController.usernameField.setText("new_user");
            signUpController.passwordField.setText("password123");

            try {
                when(preparedStatement.executeQuery()).thenReturn(resultSet);
                when(resultSet.next()).thenReturn(false); // Username does not exist
                when(preparedStatement.executeQuery()).thenReturn(resultSet);
                when(resultSet.next()).thenReturn(true); // ID exists
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Act
            signUpController.signUp(new ActionEvent());

            // Assert
            try {
                verify(preparedStatement, never()).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // Additional assertions can be added to verify alert messages
        });
    }

    @Test
    public void testSignUpEmptyFields() {
        Platform.runLater(() -> {
            // Arrange
            signUpController.idField.setText("");
            signUpController.nameField.setText("");
            signUpController.surnameField.setText("");
            signUpController.usernameField.setText("");
            signUpController.passwordField.setText("");

            // Act
            signUpController.signUp(new ActionEvent());

            // Assert
            try {
                verify(preparedStatement, never()).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // Additional assertions can be added to verify alert messages
        });
    }

    @Test
    public void testSignUpDatabaseConnectionFailure() throws SQLException {
        Platform.runLater(() -> {
            // Arrange
            signUpController.idField.setText("1");
            signUpController.nameField.setText("John");
            signUpController.surnameField.setText("Doe");
            signUpController.usernameField.setText("john_doe");
            signUpController.passwordField.setText("password123");

            signUpController.connection = null; // Simulate database connection failure

            // Act
            signUpController.signUp(new ActionEvent());

            // Assert
            try {
                verify(preparedStatement, never()).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // Additional assertions for alert messages
        });
    }

    // Regression Tests

    @Test
    public void testSignUpValidUserAfterChanges() throws SQLException {
        Platform.runLater(() -> {
            // Arrange
            signUpController.idField.setText("2");
            signUpController.nameField.setText("Jane");
            signUpController.surnameField.setText("Doe");
            signUpController.usernameField.setText("jane_doe");
            signUpController.passwordField.setText("password456");

            try {
                when(preparedStatement.executeQuery()).thenReturn(resultSet);
                when(resultSet.next()).thenReturn(false); // Username and ID do not exist
                when(preparedStatement.executeUpdate()).thenReturn(1); // Insertion is successful
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Act
            signUpController.signUp(new ActionEvent());

            // Assert
            try {
                verify(preparedStatement, times(1)).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // Additional assertions can be added to verify alert messages and navigation
        });
    }

    @Test
    public void testSignUpWithInvalidPassword() throws SQLException {
        Platform.runLater(() -> {
            // Arrange
            signUpController.idField.setText("3");
            signUpController.nameField.setText("Sam");
            signUpController.surnameField.setText("Smith");
            signUpController.usernameField.setText("sam_smith");
            signUpController.passwordField.setText(""); // Invalid password

            // Act
            signUpController.signUp(new ActionEvent());

            // Assert
            try {
                verify(preparedStatement, never()).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // Additional assertions to verify alert messages for invalid password
        });
    }

    @Test
    public void testSignUpWithValidAndInvalidData() throws SQLException {
        Platform.runLater(() -> {
            // Arrange
            signUpController.idField.setText("4");
            signUpController.nameField.setText("Alex");
            signUpController.surnameField.setText("Brown");
            signUpController.usernameField.setText("alex_brown");
            signUpController.passwordField.setText("password789");

            try {
                when(preparedStatement.executeQuery()).thenReturn(resultSet);
                when(resultSet.next()).thenReturn(true); // Username exists
                when(preparedStatement.executeQuery()).thenReturn(resultSet);
                when(resultSet.next()).thenReturn(false); // ID does not exist
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Act
            signUpController.signUp(new ActionEvent());

            // Assert
            try {
                verify(preparedStatement, never()).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // Additional assertions for alert messages
        });
    }

    @Test
    public void testSignUpAfterDatabaseRecovery() throws SQLException {
        Platform.runLater(() -> {
            // Arrange
            signUpController.idField.setText("5");
            signUpController.nameField.setText("Emily");
            signUpController.surnameField.setText("Johnson");
            signUpController.usernameField.setText("emily_johnson");
            signUpController.passwordField.setText("password101");

            try {
                when(preparedStatement.executeQuery()).thenReturn(resultSet);
                when(resultSet.next()).thenReturn(false); // Username and ID do not exist
                when(preparedStatement.executeUpdate()).thenReturn(1); // Insertion is successful
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Simulate a database connection issue, then recover
            signUpController.connection = null;
            signUpController.signUp(new ActionEvent());
            signUpController.connection = mock(Connection.class);

            // Act
            signUpController.signUp(new ActionEvent());

            // Assert
            try {
                verify(preparedStatement, times(1)).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // Additional assertions for recovery
        });
    }
}
