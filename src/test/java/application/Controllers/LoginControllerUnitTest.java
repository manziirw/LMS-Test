package application.Controllers;

import javafx.embed.swing.JFXPanel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Nested

@PrepareForTest({ FXMLLoader.class })
class LoginControllerUnitTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    public void setUp() throws SQLException {
        // Initialize JavaFX runtime
        new JFXPanel();  //  initialize JavaFX toolkit

        // Ensure that all JavaFX related code is run on the FX Application Thread
        Platform.runLater(() -> {
            MockitoAnnotations.openMocks(this);
            try {
                when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            loginController.usernameField = new TextField();
            loginController.passwordField = new PasswordField();
        });
    }

    @Test
    public void testLoginAdminSuccess() throws SQLException {
        Platform.runLater(() -> {
            // Arrange
            loginController.usernameField.setText("adminUser");
            loginController.passwordField.setText("adminPass");
            try {
                when(preparedStatement.executeQuery()).thenReturn(resultSet);
                when(resultSet.next()).thenReturn(true);
                when(resultSet.getString("role")).thenReturn("admin");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Act
            loginController.login(new ActionEvent());

            // Assert
            try {
                verify(resultSet, times(1)).getString("role");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            // Add more assertions as needed to verify the behavior
        });
    }

    @Test
    public void testLoginUserSuccess() throws SQLException {
        Platform.runLater(() -> {
            // Arrange
            loginController.usernameField.setText("normalUser");
            loginController.passwordField.setText("userPass");
            try {
                when(preparedStatement.executeQuery()).thenReturn(resultSet);
                when(resultSet.next()).thenReturn(true);
                when(resultSet.getString("role")).thenReturn("user");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Act
            loginController.login(new ActionEvent());

            // Assert
            try {
                verify(resultSet, times(1)).getString("role");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
    }

    @Test
    public void testLoginInvalidCredentials() throws SQLException {
        Platform.runLater(() -> {
            // Arrange
            loginController.usernameField.setText("invalidUser");
            loginController.passwordField.setText("invalidPass");
            try {
                when(preparedStatement.executeQuery()).thenReturn(resultSet);
                when(resultSet.next()).thenReturn(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Act
            loginController.login(new ActionEvent());

            // Assert
            try {
                verify(resultSet, never()).getString("role");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    @Test
    public void testGoToSignUp() throws IOException {
        Platform.runLater(() -> {
            // Mock the behavior of FXMLLoader
            Parent mockRoot = mock(Parent.class);
            try {
                PowerMockito.mockStatic(FXMLLoader.class);
                when(FXMLLoader.load(getClass().getResource("/application/SignUp.fxml"))).thenReturn(mockRoot);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Mock the behavior of the stage
            Stage mockStage = mock(Stage.class);
            when(loginController.usernameField.getScene().getWindow()).thenReturn(mockStage);

            // Call the goToSignUp method
            loginController.goToSignUp(new ActionEvent());

            // Verify that the scene was set correctly
            verify(mockStage).setScene(any(Scene.class));
        });
    }

    @Test
    public void testGoToSignUpWithIOException() throws IOException {
        Platform.runLater(() -> {
            // Mock the behavior to throw IOException
            try {
                PowerMockito.mockStatic(FXMLLoader.class);
                when(FXMLLoader.load(getClass().getResource("/application/SignUp.fxml"))).thenThrow(new IOException());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Call the goToSignUp method
            loginController.goToSignUp(new ActionEvent());

            // Verify that the error alert was shown
            // (You might need to add a method to verify the alert in your controller)
        });
    }
}
