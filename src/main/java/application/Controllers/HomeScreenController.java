package application.Controllers;

import application.Models.Book;
import application.Utils.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class HomeScreenController {

    @FXML
    TableView<Book> bookTableView;

    @FXML
    TableColumn<Book, String> titleColumn;

    @FXML
    TableColumn<Book, String> authorColumn;

    @FXML
    private Button borrowButton;

    @FXML
    private Button returnButton;

    @FXML
    TextField userIdField;

    @FXML
    TextField lendDateField;

    @FXML
    TextField searchField;

    ObservableList<Book> bookList = FXCollections.observableArrayList();

    Connection connection;

    @FXML
    public void initialize() {
        // Initialize TableView columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        try {
            connection = DatabaseUtil.getConnection();
            if (connection != null) {
                loadBooks();
                // Set the default items for the TableView
                bookTableView.setItems(bookList);
            } else {
                showErrorAlert("Database connection failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database connection error: " + e.getMessage());
        }
    }

    void loadBooks() {
        String query = "SELECT * FROM book";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            bookList.clear(); // Clear the existing list
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");

                Book book = new Book(id, title, author);
                bookList.add(book);
            }

            // Ensure bookTableView is not null before setting items
            if (bookTableView != null) {
                bookTableView.setItems(bookList);
            } else {
                showErrorAlert("TableView not initialized!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error loading books: " + e.getMessage());
        }
    }

    @FXML
    void searchBooks() {
        String searchQuery = searchField.getText().trim().toLowerCase();
        if (searchQuery.isEmpty()) {
            // If search field is empty, show all books
            bookTableView.setItems(bookList);
            return;
        }

        ObservableList<Book> filteredBooks = FXCollections.observableArrayList();
        for (Book book : bookList) {
            if (book.getTitle().toLowerCase().contains(searchQuery) || book.getAuthor().toLowerCase().contains(searchQuery)) {
                filteredBooks.add(book);
            }
        }

        bookTableView.setItems(filteredBooks);
    }

    @FXML
    void borrowBook() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showErrorAlert("Please select a book to borrow!");
            return;
        }

        String userId = userIdField.getText().trim();
        String lendDate = lendDateField.getText().trim();

        if (userId.isEmpty() || lendDate.isEmpty()) {
            showErrorAlert("User ID and lend date fields cannot be empty!");
            return;
        }

        // Insert lending information into the lend table
        String lendQuery = "INSERT INTO lend (book_id, user_id, lend_date) VALUES (?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(lendQuery);
            statement.setInt(1, selectedBook.getId());
            statement.setInt(2, Integer.parseInt(userId));
            statement.setDate(3, java.sql.Date.valueOf(lendDate));
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                showInfoAlert("Book borrowed successfully!");
            } else {
                showErrorAlert("Failed to borrow book!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error borrowing book: " + e.getMessage());
        }
    }

    @FXML
    void returnBook() {
        // Get the selected book
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showErrorAlert("Please select a book to return!");
            return;
        }

        // Get user ID
        String userId = userIdField.getText().trim();
        if (userId.isEmpty()) {
            showErrorAlert("User ID cannot be empty!");
            return;
        }

        // Update lending information in the lend table
        String returnQuery = "UPDATE lend SET return_date = ? WHERE book_id = ? AND user_id = ? AND return_date IS NULL";
        try {
            PreparedStatement statement = connection.prepareStatement(returnQuery);
            statement.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            statement.setInt(2, selectedBook.getId());
            statement.setInt(3, Integer.parseInt(userId));
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                showInfoAlert("Book returned successfully!");
            } else {
                showErrorAlert("Failed to return book! Make sure the you borrowed this book and hasn't already been returned.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error returning book: " + e.getMessage());
        }
    }

    short showInfoAlert(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Information", message);
        return 0;
    }

    void showErrorAlert(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
