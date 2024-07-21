package application.Controllers;

import application.Models.Lend;  // Import the Lend class
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

public class LendUnitTest {

    @Test
    public void testGettersAndSetters() {
        // Create an instance of Lend
        Lend lend = new Lend();

        // Test ID
        lend.setId(1);
        assertEquals(1, lend.getId());

        // Test Book ID
        lend.setBookId(101);
        assertEquals(101, lend.getBookId());

        // Test User ID
        lend.setUserId(202);
        assertEquals(202, lend.getUserId());

        // Test Lend Date
        Date now = new Date();
        lend.setLendDate(now);
        assertEquals(now, lend.getLendDate());
    }

    @Test
    public void testDefaultConstructor() {
        // Test default constructor
        Lend lend = new Lend();
        assertEquals(0, lend.getId());
        assertEquals(0, lend.getBookId());
        assertEquals(0, lend.getUserId());
        assertNull(lend.getLendDate());
    }

}
