package application.Controllers;

import application.Models.Book;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdminDashboardControllerUnitTest {

    private AdminDashboardController controller;
    private PreparedStatement mockStatement;
    private Connection mockConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        new JFXPanel();
        controller = new AdminDashboardController();

        // Mock the connection and prepared statement
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        controller.connection = mockConnection;

        // Initialize FXML components  for testing
        controller.bookTitleField = new TextField();
        controller.authorField = new TextField();
        controller.bookTableView = new TableView<>();
        controller.idColumn = new TableColumn<>("ID");
        controller.titleColumn = new TableColumn<>("Title");
        controller.authorColumn = new TableColumn<>("Author");
        controller.bookList = FXCollections.observableArrayList();

        // Set up TableView columns
        controller.bookTableView.getColumns().addAll(controller.idColumn, controller.titleColumn, controller.authorColumn);
        controller.idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        controller.titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        controller.authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
    }

    @Test
    public void testAddBook() throws SQLException {
        // Ensure this code runs on the JavaFX Application Thread
        Platform.runLater(() -> {
            // Set up input fields
            controller.bookTitleField.setText("New Book Title");
            controller.authorField.setText("New Book Author");

            // Mock the execution of the insert query
            try {
                when(mockStatement.executeUpdate()).thenReturn(1); // Simulate successful insert

                // Call the method to add a book
                controller.addBook();

                // Check bookList after adding
                assertEquals(1, controller.bookList.size(), "Book list should contain 1 book.");
                assertEquals("New Book Title", controller.bookList.get(0).getTitle(), "Book title should match.");
                assertEquals("New Book Author", controller.bookList.get(0).getAuthor(), "Book author should match.");

                // Verify interactions with the database
                verify(mockStatement, times(1)).executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Wait for the JavaFX thread to complete
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testLoadBooks() throws SQLException {
        // Ensure this code runs on the JavaFX Application Thread
        Platform.runLater(() -> {
            // Set up a mock result set
            var mockResultSet = mock(ResultSet.class);
            try {
                when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
                when(mockStatement.executeQuery()).thenReturn(mockResultSet);
                when(mockResultSet.next()).thenReturn(true).thenReturn(false); // Simulate one row of results
                when(mockResultSet.getInt("id")).thenReturn(1);
                when(mockResultSet.getString("title")).thenReturn("Mock Title");
                when(mockResultSet.getString("author")).thenReturn("Mock Author");

                // Initialize bookList to avoid NullPointerException
                controller.bookList = FXCollections.observableArrayList();

                // Call the method to load books
                controller.loadBooks();

                // Check bookList after loading
                assertEquals(1, controller.bookList.size(), "Book list should contain 1 book.");
                assertEquals("Mock Title", controller.bookList.get(0).getTitle(), "Book title should match.");
                assertEquals("Mock Author", controller.bookList.get(0).getAuthor(), "Book author should match.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Wait for the JavaFX thread to complete
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testEditBook() throws SQLException {
        // Ensure this code runs on the JavaFX Application Thread
        Platform.runLater(() -> {
            // Set up initial book in the list and select it
            Book initialBook = new Book(1, "Initial Title", "Initial Author");
            controller.bookList.add(initialBook);
            controller.bookTableView.setItems(controller.bookList);
            controller.bookTableView.getSelectionModel().select(initialBook);

            // Set up input fields
            controller.bookTitleField.setText("Edited Title");
            controller.authorField.setText("Edited Author");

            // Mock the execution of the update query
            try {
                when(mockStatement.executeUpdate()).thenReturn(1); // Simulate successful update

                // Call the method to edit a book
                controller.editBook();

                // Check bookList after editing
                assertEquals(1, controller.bookList.size(), "Book list should still contain 1 book.");
                assertEquals("Edited Title", controller.bookList.get(0).getTitle(), "Book title should match the edited title.");
                assertEquals("Edited Author", controller.bookList.get(0).getAuthor(), "Book author should match the edited author.");

                // Verify interactions with the database
                verify(mockStatement, times(1)).executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Wait for the JavaFX thread to complete
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testDeleteBook() throws SQLException {
        // Ensure this code runs on the JavaFX Application Thread
        Platform.runLater(() -> {
            // Set up initial book in the list and select it
            Book initialBook = new Book(1, "Title to Delete", "Author to Delete");
            controller.bookList.add(initialBook);
            controller.bookTableView.setItems(controller.bookList);
            controller.bookTableView.getSelectionModel().select(initialBook);

            // Mock the execution of the delete query
            try {
                when(mockStatement.executeUpdate()).thenReturn(1); // Simulate successful delete

                // Call the method to delete a book
                controller.deleteBook();

                // Check bookList after deleting
                assertEquals(0, controller.bookList.size(), "Book list should be empty after deletion.");

                // Verify interactions with the database
                verify(mockStatement, times(1)).executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Wait for the JavaFX thread to complete
        WaitForAsyncUtils.waitForFxEvents();
    }
}
