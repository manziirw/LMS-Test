package application.Controllers;



import application.Models.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserUnitTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        int expectedId = 1;
        String expectedUsername = "john_doe";
        String expectedPassword = "password123";

        // Act
        User user = new User(expectedId, expectedUsername, expectedPassword);

        // Assert
        assertEquals(expectedId, user.getId());
        assertEquals(expectedUsername, user.getUsername());
        assertEquals(expectedPassword, user.getPassword());
    }

}

