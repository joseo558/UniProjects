package pt.pa.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateTest {
    Coordinate c;

    @BeforeEach
    void setUp() {
        c = new Coordinate(1, 2);
    }

    @Test
    void setPosX() {
        assertEquals(1, c.getPosX());
        assertEquals(2, c.getPosY());
        c.setPosX(3);
        assertEquals(3, c.getPosX());
        assertEquals(2, c.getPosY()); // y didn't change
    }

    @Test
    void setPosY() {
        assertEquals(1, c.getPosX());
        assertEquals(2, c.getPosY());
        c.setPosY(4);
        assertEquals(4, c.getPosY());
        assertEquals(1, c.getPosX()); // x didn't change
    }

    @Test
    void testEquals() {
        // same
        Coordinate c2 = c;
        Coordinate c3 = new Coordinate(1, 2);
        assertEquals(c, c2);
        assertEquals(c, c3);

        // different
        Coordinate c4 = new Coordinate(3, 4);
        Coordinate c5 = new Coordinate(1, 4); // different y
        Coordinate c6 = new Coordinate(3, 2); // different x
        assertNotEquals(c, c4);
        assertNotEquals(c, c5);
        assertNotEquals(c, c6);
    }

    @Test
    void testHashCode() {
        // same
        Coordinate c2 = c;
        Coordinate c3 = new Coordinate(1, 2);
        assertEquals(c.hashCode(), c2.hashCode());
        assertEquals(c.hashCode(), c3.hashCode());

        // different
        Coordinate c4 = new Coordinate(3, 4);
        Coordinate c5 = new Coordinate(1, 4);
        Coordinate c6 = new Coordinate(3, 2);
        assertNotEquals(c.hashCode(), c4.hashCode());
        assertNotEquals(c.hashCode(), c5.hashCode());
        assertNotEquals(c.hashCode(), c6.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("(1, 2)", c.toString());
    }
}