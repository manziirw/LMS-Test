package application.Controllers;

import application.Models.Book;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class HomeScreenControllerUnitTest {

    private HomeScreenController controller;
    private PreparedStatement mockStatement;
    private Connection mockConnection;

    @BeforeEach
    public void setUp() throws SQLException {
        new JFXPanel();
        controller = new HomeScreenController();

        // Mock the connection and prepared statement
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        controller.connection = mockConnection;

        // Initialize FXML components manually for testing
        controller.bookTableView = new TableView<>();
        controller.titleColumn = new TableColumn<>("Title");
        controller.authorColumn = new TableColumn<>("Author");
        controller.userIdField = new TextField();
        controller.lendDateField = new TextField();
        controller.searchField = new TextField();
        controller.bookList = FXCollections.observableArrayList();

        // Set up TableView columns
        controller.bookTableView.getColumns().addAll(controller.titleColumn, controller.authorColumn);
        controller.titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        controller.authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
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
    public void testSearchBooks() {
        Platform.runLater(() -> {
            // Add books to the bookList
            controller.bookList.addAll(
                    new Book(1, "Java Programming", "Author One"),
                    new Book(2, "Python Programming", "Author Two"),
                    new Book(3, "Effective Java", "Author One"),
                    new Book(4, "Clean Code", "Author Three")
            );

            // Test empty search field (should show all books)
            controller.searchField.setText("");
            controller.searchBooks();
            assertEquals(4, controller.bookTableView.getItems().size(), "Should display all books when search field is empty.");

            // Test searching by title
            controller.searchField.setText("java");
            controller.searchBooks();
            assertEquals(2, controller.bookTableView.getItems().size(), "Should display 2 books containing 'java' in the title or author.");

            // Test searching by author
            controller.searchField.setText("author one");
            controller.searchBooks();
            assertEquals(2, controller.bookTableView.getItems().size(), "Should display 2 books written by 'Author One'.");

            // Test searching with no results
            controller.searchField.setText("nonexistent");
            controller.searchBooks();
            assertEquals(0, controller.bookTableView.getItems().size(), "Should display 0 books for a search with no matching results.");
        });

        // Wait for the JavaFX thread to complete
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testBorrowBook() throws SQLException {
        Platform.runLater(() -> {
            // Add a book to the bookList and select it
            Book book = new Book(1, "Test Book", "Test Author");
            controller.bookList.add(book);
            controller.bookTableView.setItems(controller.bookList);
            controller.bookTableView.getSelectionModel().select(book);

            // Set valid user ID and lend date
            controller.userIdField.setText("1");
            controller.lendDateField.setText("2024-07-20");

            try {
                // Mock successful execution
                when(mockStatement.executeUpdate()).thenReturn(1);

                // Call borrowBook method
                controller.borrowBook();

                // Verify that the SQL query was executed
                verify(mockStatement, times(1)).executeUpdate();

                // Check the alert message
                assertEquals("Book borrowed successfully!", controller.showInfoAlert("Book borrowed successfully!"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Wait for the JavaFX thread to complete
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testReturnBook() throws SQLException {
        // Mock successful execution outside of the Platform.runLater block
        when(mockStatement.executeUpdate()).thenReturn(1);

        Platform.runLater(() -> {
            // Add a book to the bookList and select it
            Book book = new Book(1, "Test Book", "Test Author");
            controller.bookList.add(book);
            controller.bookTableView.setItems(controller.bookList);
            controller.bookTableView.getSelectionModel().select(book);

            // Call returnBook method
            controller.returnBook();

            // Verify that the SQL query was executed
            try {
                verify(mockStatement, times(1)).executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // Check the alert message
            assertEquals("Book returned successfully!", controller.showInfoAlert("Book returned successfully!"));
        });

        // Wait for the JavaFX thread to complete
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testShowInfoAlert() {
        Platform.runLater(() -> {
            // Set up a spy for the HomeScreenController
            HomeScreenController spyController = spy(controller);

            // Call the showInfoAlert method
            spyController.showInfoAlert("Test Info Message");

            // Verify that showAlert was called with the correct parameters
            verify(spyController).showAlert(Alert.AlertType.INFORMATION, "Information", "Test Info Message");
        });

        // Wait for the JavaFX thread to complete
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testShowErrorAlert() {
        Platform.runLater(() -> {
            // Set up a spy for the HomeScreenController
            HomeScreenController spyController = spy(controller);

            // Call the showErrorAlert method
            spyController.showErrorAlert("Test Error Message");

            // Verify that showAlert was called with the correct parameters
            verify(spyController).showAlert(Alert.AlertType.ERROR, "Error", "Test Error Message");
        });

        // Wait for the JavaFX thread to complete
        WaitForAsyncUtils.waitForFxEvents();
    }
}
