package application.Controllers;

import application.Utils.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    public TextField usernameField;

    @FXML
    public PasswordField passwordField;

    public Connection connection;

    @FXML
    public void initialize() {
        // Initialize database connection
        try {
            connection = DatabaseUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database connection error!");
        }
    }

    @FXML
    public void login(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Query to check credentials and role
        String query = "SELECT role FROM user WHERE username = ? AND password = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");

                if ("admin".equals(role)) {
                    // Admin login successful
                    showInfoAlert("Admin login successful!");
                    navigateToAdminDashboard();
                } else {
                    // Normal user login successful
                    showInfoAlert("User login successful!");
                    navigateToHomeScreen();
                }
            } else {
                // Invalid credentials
                showErrorAlert("Invalid username or password!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error logging in. Please try again.");
        }
    }

    @FXML
    public void goToSignUp(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/SignUp.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 400));
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error loading sign up page!");
        }
    }

    void navigateToAdminDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/AdminDashboard.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600)); // Adjust dimensions as needed
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error loading admin dashboard!");
        }
    }

    void navigateToHomeScreen() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/HomeScreen.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600)); // Adjust dimensions as needed
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error loading home screen! " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
