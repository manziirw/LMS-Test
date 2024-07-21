package application.Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Book {
    private final StringProperty title;
    private final StringProperty author;
    private final int id; // Add ID property

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
    }

    // Getters and setters for ID, title, and author
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title.get();
    }


    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

}
