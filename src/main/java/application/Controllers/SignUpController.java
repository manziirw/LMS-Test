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

public class SignUpController {

    @FXML
    public TextField idField;

    @FXML
    public TextField nameField;

    @FXML
    public TextField surnameField;

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
            showErrorAlert("Failed to connect to the database. Please try again later.");
        }
    }

    @FXML
    public void signUp(ActionEvent event) {
        if (connection == null) {
            showErrorAlert("Database connection is not available. Please try again later.");
            return;
        }

        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showErrorAlert("All fields are required.");
            return;
        }

        // Check if the username already exists
        if (isUsernameExists(username)) {
            showErrorAlert("Username already exists. Please choose another one.");
            return;
        }
        if(isIdExists(Integer.parseInt(id))){
            showErrorAlert("ID already exists you may choose another one to proceed");
            return;
        }

        // Query to insert new user
        String query = "INSERT INTO user (id, name, surname, username, password) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(id));
            statement.setString(2, name);
            statement.setString(3, surname);
            statement.setString(4, username);
            statement.setString(5, password);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                // User successfully registered
                showInfoAlert("Sign Up successful!");
                backToLogin(event); // Navigate back to login after successful sign up
            } else {
                // Failed to register user
                showErrorAlert("Failed to sign up!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error occurred while signing up. Please try again.");
        }
    }

    @FXML
    void backToLogin(ActionEvent event) {
        // Navigate back to login screen
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/Login.fxml"));
            Stage stage = (Stage) idField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 300));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM user WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean isIdExists(int id){
        String query = "SELECT * FROM user where id=? ";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {

                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
