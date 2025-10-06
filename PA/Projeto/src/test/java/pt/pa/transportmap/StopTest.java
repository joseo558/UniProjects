package pt.pa.transportmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Stop class
 */
class StopTest {
    /** The Stop object (fixture) */
    Stop stop;

    @BeforeEach
    void setUp() {
        stop = new Stop("1", "Stop1", 1.0, 1.0);
    }

    @Test
    void setCode() {
        assertThrows(IllegalArgumentException.class, () -> stop.setCode(null));
        assertThrows(IllegalArgumentException.class, () -> stop.setCode(""));
        stop.setCode("2");
        assertEquals("2", stop.getCode());
    }

    @Test
    void setName() {
        assertThrows(IllegalArgumentException.class, () -> stop.setName(null));
        assertThrows(IllegalArgumentException.class, () -> stop.setName(""));
        stop.setName("Stop2");
        assertEquals("Stop2", stop.getName());
    }

    @Test
    void setLatitude() {
        stop.setLatitude(2.0);
        assertEquals(2.0, stop.getLatitude());
    }

    @Test
    void setLongitude() {
        stop.setLongitude(2.0);
        assertEquals(2.0, stop.getLongitude());
    }

    @Test
    void testEquals() {
        // same
        Stop stop2 = stop;
        Stop stop3 = new Stop("1", "Stop1", 1.0, 1.0);
        assertEquals(stop, stop2);
        assertEquals(stop, stop3);

        // different
        Stop stop4 = new Stop("2", "Stop2", 2.0, 2.0);
        assertNotEquals(stop, stop4);

        // equal since only stop code is taken into account
        Stop stop5 = new Stop("1", "Stop20", 20.0, 20.0);
        assertEquals(stop, stop5);

        // assert throws
        assertThrows(IllegalArgumentException.class, () -> new Stop(null, null, 20.0, 30.0));
        assertThrows(IllegalArgumentException.class, () -> new Stop(null, "a", 20.0, 30.0));
        assertThrows(IllegalArgumentException.class, () -> new Stop("b", null, 20.0, 30.0));

        assertThrows(IllegalArgumentException.class, () -> new Stop("", "", 20.0, 30.0));
        assertThrows(IllegalArgumentException.class, () -> new Stop("", null, 20.0, 30.0));
        assertThrows(IllegalArgumentException.class, () -> new Stop("", "a", 20.0, 30.0));
        assertThrows(IllegalArgumentException.class, () -> new Stop(null, "", 20.0, 30.0));
        assertThrows(IllegalArgumentException.class, () -> new Stop("b", "", 20.0, 30.0));
    }

    @Test
    void testHashCode() {
        // same
        Stop stop2 = stop;
        Stop stop3 = new Stop("1", "Stop1", 1.0, 1.0);
        assertEquals(stop.hashCode(), stop2.hashCode());
        assertEquals(stop.hashCode(), stop3.hashCode());

        // different
        Stop stop4 = new Stop("2", "Stop2", 2.0, 2.0);
        assertNotEquals(stop.hashCode(), stop4.hashCode());

        // equal hashcode since only stop code is taken into account
        Stop stop5 = new Stop("1", "Stop20", 20.0, 20.0);
        assertEquals(stop.hashCode(), stop5.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("Stop1", stop.toString());
    }

    @Test
    void toStringAll() {
        assertEquals("Stop1:\n" +
                "Latitude: 1.0\n" +
                "Longitude: 1.0", stop.toStringAll());
    }
}