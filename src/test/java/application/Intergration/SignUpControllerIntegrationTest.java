package application.Intergration;

import application.Controllers.SignUpController;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class SignUpControllerIntegrationTest {

    private Connection connection;
    private SignUpController signUpController;

    @BeforeEach
    public void setUp() throws SQLException, InterruptedException {
        // Initialize JavaFX environment
        new JFXPanel();

        // Set up H2 in-memory database
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        initializeDatabase();

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            // Initialize controller and fields
            signUpController = new SignUpController();
            initializeTextFields();
            signUpController.connection = connection;

            latch.countDown(); // Signal that initialization is complete
        });

        latch.await(5, TimeUnit.SECONDS); // Wait for JavaFX initialization
    }

    private void initializeDatabase() throws SQLException {
        // Create table if it does not already exist
        try (PreparedStatement stmt = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, name VARCHAR(255), surname VARCHAR(255), username VARCHAR(255), password VARCHAR(255))")) {
            stmt.executeUpdate();
        }
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

            // Execute the sign-up operation
            try {
                signUpController.signUp(null);

                // Verify the data was inserted
                try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
                    stmt.setInt(1, 1);
                    try (ResultSet rs = stmt.executeQuery()) {
                        assertTrue(rs.next(), "User should be inserted into the database.");
                        assertEquals("John", rs.getString("name"));
                        assertEquals("Doe", rs.getString("surname"));
                        assertEquals("johndoe", rs.getString("username"));
                        assertEquals("password", rs.getString("password"));
                    }
                }
            } catch (Exception e) {
                fail("Exception occurred during sign up: " + e.getMessage());
            }
        });
    }

    @Test
    public void testSignUpFailure() throws SQLException {
        Platform.runLater(() -> {
            setUpTextFields("1", "John", "Doe", "johndoe", "password");

            try {
                // Simulate a failure scenario (e.g., duplicate ID or username)
                // Assuming that the controller should handle failure properly.
                signUpController.signUp(null);

                // Verify the data was not inserted if it should fail
                try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
                    stmt.setInt(1, 1);
                    try (ResultSet rs = stmt.executeQuery()) {
                        assertFalse(rs.next(), "User should not be inserted into the database due to failure.");
                    }
                }
            } catch (Exception e) {
                // Expected failure, handle accordingly
                assertTrue(e.getMessage().contains("Error occurred while signing up"), "Expected failure due to error.");
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
