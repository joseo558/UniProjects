package pt.pa.transportmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pa.transportmap.path.PathCriteria;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for RouteInfo class
 */
class RouteInfoTest {
    /** The RouteInfo object (fixture) */
    private RouteInfo routeInfo;
    // String constants to be used in tests
    /** Text for the PathCriteria Distance */
    static final String distanceString = PathCriteria.DISTANCE.toString();
    /** Text for the PathCriteria Duration */
    static final String durationString = PathCriteria.DURATION.toString();
    /** Text for the PathCriteria Sustainability */
    static final String sustainabilityString = PathCriteria.SUSTAINABILITY.toString();
    /** Text for the PathCriteria Distance unit */
    static final String distanceUnit = PathCriteria.DISTANCE.getUnit();
    /** Text for the PathCriteria Duration unit */
    static final String durationUnit = PathCriteria.DURATION.getUnit();
    /** Text for the PathCriteria Sustainability unit */
    static final String sustainabilityUnit = PathCriteria.SUSTAINABILITY.getUnit();

    /**
     * Appends the expected route info to the StringBuilder
     * @param sb StringBuilder to append the info
     * @param distance double distance value
     * @param duration double duration value
     * @param sustainability double sustainability value
     */
    private void appendExpectedRouteInfo(StringBuilder sb, double distance, double duration, double sustainability){
        if(sb == null){ return; }
        sb.append(distanceString).append(": ").append(String.format("%.1f", distance)).append(" ").append(distanceUnit).append("\n")
                .append(durationString).append(": ").append(String.format("%.0f", duration)).append(" ").append(durationUnit).append("\n")
                .append(sustainabilityString).append(": ").append(String.format("%.2f", sustainability)).append(" ").append(sustainabilityUnit);
    }

    /** Create fixture */
    @BeforeEach
    void setUp() {
        routeInfo = new RouteInfo(10.2, 20.5, 30.8);
    }

    @Test
    void setDistance() {
        assertEquals(10.2, routeInfo.getDistance()); // has to be 10.2
        assertThrows(IllegalArgumentException.class, () -> routeInfo.setDistance(-10.2)); // must be positive
        routeInfo.setDistance(20.2);
        assertEquals(20.2, routeInfo.getDistance()); // has to be 20.2
    }

    @Test
    void setDuration() {
        assertEquals(20.5, routeInfo.getDuration()); // has to be 20.5
        assertThrows(IllegalArgumentException.class, () -> routeInfo.setDuration(-20.5)); // must be positive
        routeInfo.setDuration(30.5);
        assertEquals(30.5, routeInfo.getDuration()); // has to be 30.5
    }

    @Test
    void setCost() {
        assertEquals(30.8, routeInfo.getSustainability()); // has to be 30.8
        routeInfo.setSustainability(-30.8);
        assertEquals(-30.8, routeInfo.getSustainability()); // has to be -30.8
    }

    @Test
    void testEquals() {
        // same
        RouteInfo routeInfo2 = routeInfo;
        RouteInfo routeInfo3 = new RouteInfo(10.2, 20.5, 30.8);

        RouteInfo routeInfo4 = new RouteInfo(10.1, 20.5, 30.8); // different distance
        RouteInfo routeInfo5 = new RouteInfo(10.2, 20.4, 30.8); // different duration
        RouteInfo routeInfo6 = new RouteInfo(10.2, 20.5, 30.7); // different cost
        RouteInfo routeInfo7 = new RouteInfo(10.1, 20.4, 30.8); // different distance and duration
        RouteInfo routeInfo8 = new RouteInfo(10.1, 20.5, 30.7); // different distance and cost
        RouteInfo routeInfo9 = new RouteInfo(10.2, 20.4, 30.7); // different duration and cost
        RouteInfo routeInfo10 = new RouteInfo(10.1, 20.4, 30.7); // different distance, duration and cost

        assertEquals(routeInfo, routeInfo2); // same
        assertEquals(routeInfo, routeInfo3); // same

        assertNotEquals(routeInfo, routeInfo4); // different distance
        assertNotEquals(routeInfo, routeInfo5); // different duration
        assertNotEquals(routeInfo, routeInfo6); // different cost
        assertNotEquals(routeInfo, routeInfo7); // different distance and duration
        assertNotEquals(routeInfo, routeInfo8); // different distance and cost
        assertNotEquals(routeInfo, routeInfo9); // different duration and cost
        assertNotEquals(routeInfo, routeInfo10); // different distance, duration and cost
    }

    @Test
    void testHashCode() {
        RouteInfo routeInfo2 = routeInfo;
        RouteInfo routeInfo3 = new RouteInfo(10.2, 20.5, 30.8);
        assertEquals(routeInfo.hashCode(), routeInfo2.hashCode());
        assertEquals(routeInfo.hashCode(), routeInfo3.hashCode());

        RouteInfo routeInfo4 = new RouteInfo(10.1, 20.5, 30.8); // different distance
        RouteInfo routeInfo5 = new RouteInfo(10.2, 20.4, 30.8); // different duration
        RouteInfo routeInfo6 = new RouteInfo(10.2, 20.5, 30.7); // different cost
        RouteInfo routeInfo7 = new RouteInfo(10.1, 20.4, 30.8); // different distance and duration
        RouteInfo routeInfo8 = new RouteInfo(10.1, 20.5, 30.7); // different distance and cost
        RouteInfo routeInfo9 = new RouteInfo(10.2, 20.4, 30.7); // different duration and cost
        RouteInfo routeInfo10 = new RouteInfo(10.1, 20.4, 30.7); // different distance, duration and cost

        assertNotEquals(routeInfo.hashCode(), routeInfo4.hashCode()); // different distance
        assertNotEquals(routeInfo.hashCode(), routeInfo5.hashCode()); // different duration
        assertNotEquals(routeInfo.hashCode(), routeInfo6.hashCode()); // different cost
        assertNotEquals(routeInfo.hashCode(), routeInfo7.hashCode()); // different distance and duration
        assertNotEquals(routeInfo.hashCode(), routeInfo8.hashCode()); // different distance and cost
        assertNotEquals(routeInfo.hashCode(), routeInfo9.hashCode()); // different duration and cost
        assertNotEquals(routeInfo.hashCode(), routeInfo10.hashCode()); // different distance, duration and cost
    }

    @Test
    void testToString() {
        StringBuilder sb = new StringBuilder();
        // 10.2 formatted to 1 decimal place, 20.5 formatted to 0 decimal places, 30.8 formatted to 2 decimal places
        // expected string matching the format in routeInfo.toString()
        appendExpectedRouteInfo(sb, 10.2, 20.5, 30.8);
        assertEquals(sb.toString(), routeInfo.toString());
    }
}