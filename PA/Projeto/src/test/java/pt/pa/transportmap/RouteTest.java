package pt.pa.transportmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pa.transportmap.path.PathCriteria;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Route class
 */
class RouteTest {
    /** Route object to be used in tests */
    Route route;
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

    @BeforeEach
    void setUp() {
        route = new Route();
        route.addTransport(TransportType.TRAIN, new RouteInfo( 0.4, 40.4, 4.4));
        route.addTransport(TransportType.BUS, new RouteInfo( 0.5, 50.5, 5.5));
        route.addTransport(TransportType.BICYCLE, new RouteInfo( 0.6, 60.6, -6.6));
        route.addTransport(TransportType.WALK, new RouteInfo( 1.2, 120.120, -12.12));
    }

    @Test
    void getTransportList() {
        assertEquals(4, route.getTransportList().size()); // has 4 transports
        assertTrue(route.getTransportList().contains(TransportType.TRAIN));
        assertTrue(route.getTransportList().contains(TransportType.BUS));
        assertTrue(route.getTransportList().contains(TransportType.BICYCLE));
        assertTrue(route.getTransportList().contains(TransportType.WALK));

        route.removeTransport(TransportType.TRAIN);
        assertEquals(3, route.getTransportList().size()); // has 3 transports

        Route emptyRoute = new Route();
        assertEquals(0, emptyRoute.getTransportList().size()); // has 0 transports
    }

    @Test
    void getTransportInfo() {
        // Returns string with transport info
        StringBuilder sb = new StringBuilder();
        appendExpectedRouteInfo(sb, 0.4, 40, 4.40);
        assertEquals(sb.toString(), route.getTransportInfo(TransportType.TRAIN).toString());

        sb.setLength(0); // clear the StringBuilder
        appendExpectedRouteInfo(sb, 0.5, 51, 5.50);
        assertEquals(sb.toString(), route.getTransportInfo(TransportType.BUS).toString());

        sb.setLength(0); // clear the StringBuilder
        appendExpectedRouteInfo(sb, 0.6, 61, -6.60);
        assertEquals(sb.toString(), route.getTransportInfo(TransportType.BICYCLE).toString());

        sb.setLength(0); // clear the StringBuilder
        appendExpectedRouteInfo(sb, 1.2, 120, -12.12);
        assertEquals(sb.toString(), route.getTransportInfo(TransportType.WALK).toString());

        assertThrows(IllegalArgumentException.class, () -> route.getTransportInfo(TransportType.BOAT));
    }

    @Test
    void getTransportDistance() {
        // Returns distance in km
        assertEquals(0.4, route.getTransportDistance(TransportType.TRAIN));
        assertEquals(0.5, route.getTransportDistance(TransportType.BUS));
        assertEquals(0.6, route.getTransportDistance(TransportType.BICYCLE));
        assertEquals(1.2, route.getTransportDistance(TransportType.WALK));

        assertThrows(IllegalArgumentException.class, () -> route.getTransportDistance(TransportType.BOAT));
    }

    @Test
    void getTransportDuration() {
        // Returns duration in minutes
        assertEquals(40.4, route.getTransportDuration(TransportType.TRAIN));
        assertEquals(50.5, route.getTransportDuration(TransportType.BUS));
        assertEquals(60.6, route.getTransportDuration(TransportType.BICYCLE));
        assertEquals(120.120, route.getTransportDuration(TransportType.WALK));

        assertThrows(IllegalArgumentException.class, () -> route.getTransportDuration(TransportType.BOAT));
    }

    @Test
    void getTransportSustainability() {
        // Returns cost in carbons
        assertEquals(4.4, route.getTransportSustainability(TransportType.TRAIN));
        assertEquals(5.5, route.getTransportSustainability(TransportType.BUS));
        assertEquals(-6.6, route.getTransportSustainability(TransportType.BICYCLE));
        assertEquals(-12.12, route.getTransportSustainability(TransportType.WALK));

        assertThrows(IllegalArgumentException.class, () -> route.getTransportSustainability(TransportType.BOAT));
    }

    @Test
    void isEmpty() {
        Route emptyRoute = new Route();
        assertTrue(emptyRoute.isEmpty());

        assertFalse(route.isEmpty());
    }

    @Test
    void addTransport() {
        assertEquals(4, route.getTransportList().size()); // has 4 transports
        assertFalse(route.getTransportList().contains(TransportType.BOAT));

        route.addTransport(TransportType.BOAT, new RouteInfo( 0.7, 70.7, 7.7));
        assertEquals(5, route.getTransportList().size()); // has 5 transports
        assertTrue(route.getTransportList().contains(TransportType.BOAT));

        // Replaces an existing transport
        route.addTransport(TransportType.TRAIN, new RouteInfo( 0.8, 80.8, 8.8));
        assertEquals(5, route.getTransportList().size()); // still has 5 transports

        StringBuilder sb = new StringBuilder();
        appendExpectedRouteInfo(sb, 0.8, 81, 8.80);
        assertEquals(sb.toString(), route.getTransportInfo(TransportType.TRAIN).toString());

        assertThrows(IllegalArgumentException.class, () -> route.addTransport(null, new RouteInfo( 0.7, 70.7, 7.7)));
        assertThrows(IllegalArgumentException.class, () -> route.addTransport(TransportType.BOAT, null));
        assertThrows(IllegalArgumentException.class, () -> route.addTransport(null, null));
    }

    @Test
    void addTransport2() {
        assertEquals(4, route.getTransportList().size()); // has 4 transports
        assertFalse(route.getTransportList().contains(TransportType.BOAT));

        route.addTransport(TransportType.BOAT, 0.7, 70.7, 7.7);
        assertEquals(5, route.getTransportList().size()); // has 5 transports
        assertTrue(route.getTransportList().contains(TransportType.BOAT));

        // Replaces an existing transport
        route.addTransport(TransportType.TRAIN, 0.8, 80.8, 8.8);
        assertEquals(5, route.getTransportList().size()); // still has 5 transports

        StringBuilder sb = new StringBuilder();
        appendExpectedRouteInfo(sb, 0.8, 81, 8.80);
        assertEquals(sb.toString(), route.getTransportInfo(TransportType.TRAIN).toString());

        assertThrows(IllegalArgumentException.class, () -> route.addTransport(null, 0.7, 70.7, 7.7));
        assertThrows(IllegalArgumentException.class, () -> route.addTransport(TransportType.BOAT, 0.7, -70.7, 7.7));
        assertThrows(IllegalArgumentException.class, () -> route.addTransport(TransportType.BOAT, -0.7, 70.7, 7.7));
    }

    @Test
    void hasTransport() {
        assertTrue(route.hasTransport(TransportType.TRAIN));
        assertTrue(route.hasTransport(TransportType.BUS));
        assertTrue(route.hasTransport(TransportType.BICYCLE));
        assertTrue(route.hasTransport(TransportType.WALK));

        assertFalse(route.hasTransport(TransportType.BOAT));

        assertFalse(route.hasTransport(null));
        Route emptyRoute = new Route();
        assertFalse(emptyRoute.hasTransport(TransportType.TRAIN));
    }

    @Test
    void removeTransport() {
        assertEquals(4, route.getTransportList().size()); // has 4 transports
        assertTrue(route.getTransportList().contains(TransportType.TRAIN));

        assertFalse(route.removeTransport(TransportType.BOAT)); // false if not removed since it doesn't exist

        route.removeTransport(TransportType.TRAIN);
        assertEquals(3, route.getTransportList().size()); // has 3 transports
        assertFalse(route.getTransportList().contains(TransportType.TRAIN));

        route.removeTransport(TransportType.BUS);
        assertEquals(2, route.getTransportList().size()); // has 2 transports
        assertFalse(route.getTransportList().contains(TransportType.BUS));

        route.removeTransport(TransportType.BICYCLE);
        route.removeTransport(TransportType.WALK);
        assertEquals(0, route.getTransportList().size()); // has 0 transports

        assertFalse(route.removeTransport(null));
        assertFalse(route.removeTransport(TransportType.BUS));
    }

    @Test
    void setTransportInfo() {
        assertEquals(4, route.getTransportList().size()); // has 4 transports
        assertTrue(route.getTransportList().contains(TransportType.TRAIN));

        route.setTransportInfo(TransportType.TRAIN, new RouteInfo( 0.8, 80.8, 8.8));
        assertEquals(4, route.getTransportList().size()); // has 4 transports

        StringBuilder sb = new StringBuilder();
        appendExpectedRouteInfo(sb, 0.8, 81, 8.80);
        assertEquals(sb.toString(), route.getTransportInfo(TransportType.TRAIN).toString());

        assertThrows(IllegalArgumentException.class, () -> route.setTransportInfo(null, new RouteInfo( 0.7, 70.7, 7.7))); // type null
        assertThrows(IllegalArgumentException.class, () -> route.setTransportInfo(TransportType.BOAT, null)); // type not in route
        assertThrows(IllegalArgumentException.class, () -> route.setTransportInfo(TransportType.TRAIN, null)); // info null (from addTransport)
    }

    @Test
    void setTransportInfo2() {
        assertEquals(4, route.getTransportList().size()); // has 4 transports
        assertTrue(route.getTransportList().contains(TransportType.TRAIN));

        route.setTransportInfo(TransportType.TRAIN, 0.8, 80.8, 8.8);
        assertEquals(4, route.getTransportList().size()); // has 4 transports

        StringBuilder sb = new StringBuilder();
        appendExpectedRouteInfo(sb, 0.8, 81, 8.80);
        assertEquals(sb.toString(), route.getTransportInfo(TransportType.TRAIN).toString());

        assertThrows(IllegalArgumentException.class, () -> route.setTransportInfo(null, 0.7, 70.7, 7.7)); // type null
        assertThrows(IllegalArgumentException.class, () -> route.setTransportInfo(TransportType.BOAT, 0.7, 70.7, 7.7)); // type not in route
        assertThrows(IllegalArgumentException.class, () -> route.setTransportInfo(TransportType.TRAIN, 0.7, -70.7, 7.7)); // invalid duration
        assertThrows(IllegalArgumentException.class, () -> route.setTransportInfo(TransportType.TRAIN, -0.7, 70.7, 7.7)); // invalid distance
    }

    @Test
    void testEquals() {
        // same
        Route route2 = route;

        Route route3 = new Route();
        route3.addTransport(TransportType.TRAIN, new RouteInfo( 0.4, 40.4, 4.4));
        route3.addTransport(TransportType.BUS, new RouteInfo( 0.5, 50.5, 5.5));
        route3.addTransport(TransportType.BICYCLE, new RouteInfo( 0.6, 60.6, -6.6));
        route3.addTransport(TransportType.WALK, new RouteInfo( 1.2, 120.120, -12.12));

        assertEquals(route, route2);
        assertEquals(route, route3);

        // different
        Route route4 = new Route();
        route4.addTransport(TransportType.TRAIN, new RouteInfo( 0.4, 40.4, 4.4));
        route4.addTransport(TransportType.BUS, new RouteInfo( 0.5, 50.5, 5.5));
        route4.addTransport(TransportType.BICYCLE, new RouteInfo( 0.6, 60.6, -6.6));
        route4.addTransport(TransportType.WALK, new RouteInfo( 1.2, 120.120, -12.12));
        route4.addTransport(TransportType.BOAT, new RouteInfo( 0.7, 70.7, 7.7));
        assertNotEquals(route, route4);

        Route route5 = new Route(); // same number of transports and same type, but walk has different distance
        route5.addTransport(TransportType.TRAIN, new RouteInfo( 0.4, 40.4, 4.4));
        route5.addTransport(TransportType.BUS, new RouteInfo( 0.5, 50.5, 5.5));
        route5.addTransport(TransportType.BICYCLE, new RouteInfo( 0.6, 60.6, -6.6));
        route5.addTransport(TransportType.WALK, new RouteInfo( 1.5, 120.120, -12.12)); // different distance
        assertNotEquals(route, route5);
    }

    @Test
    void testHashCode() {
        // same
        Route route2 = route;

        Route route3 = new Route();
        route3.addTransport(TransportType.TRAIN, new RouteInfo( 0.4, 40.4, 4.4));
        route3.addTransport(TransportType.BUS, new RouteInfo( 0.5, 50.5, 5.5));
        route3.addTransport(TransportType.BICYCLE, new RouteInfo( 0.6, 60.6, -6.6));
        route3.addTransport(TransportType.WALK, new RouteInfo( 1.2, 120.120, -12.12));

        assertEquals(route.hashCode(), route2.hashCode());
        assertEquals(route.hashCode(), route3.hashCode());

        // different
        Route route4 = new Route();
        route4.addTransport(TransportType.TRAIN, new RouteInfo( 0.4, 40.4, 4.4));
        route4.addTransport(TransportType.BUS, new RouteInfo( 0.5, 50.5, 5.5));
        route4.addTransport(TransportType.BICYCLE, new RouteInfo( 0.6, 60.6, -6.6));
        route4.addTransport(TransportType.WALK, new RouteInfo( 1.2, 120.120, -12.12));
        route4.addTransport(TransportType.BOAT, new RouteInfo( 0.7, 70.7, 7.7));
        assertNotEquals(route.hashCode(), route4.hashCode());

        Route route5 = new Route(); // same number of transports and same type, but walk has different distance
        route5.addTransport(TransportType.TRAIN, new RouteInfo( 0.4, 40.4, 4.4));
        route5.addTransport(TransportType.BUS, new RouteInfo( 0.5, 50.5, 5.5));
        route5.addTransport(TransportType.BICYCLE, new RouteInfo( 0.6, 60.6, -6.6));
        route5.addTransport(TransportType.WALK, new RouteInfo( 1.5, 120.120, -12.12)); // different distance
        assertNotEquals(route.hashCode(), route5.hashCode());
    }

    @Test
    void testToString() {
        StringBuilder sb = new StringBuilder("Transportes disponíveis").append(": ").append("\n")
                .append("\n").append("\t").append(TransportType.TRAIN).append(": ").append("\n");
        appendExpectedRouteInfo(sb, 0.4, 40, 4.40);

        sb.append("\n").append("\t").append(TransportType.BUS).append(": ").append("\n");
        appendExpectedRouteInfo(sb, 0.5, 51, 5.50);

        sb.append("\n").append("\t").append(TransportType.WALK).append(": ").append("\n");
        appendExpectedRouteInfo(sb, 1.2, 120, -12.12);

        sb.append("\n").append("\t").append(TransportType.BICYCLE).append(": ").append("\n");
        appendExpectedRouteInfo(sb, 0.6, 61, -6.60);

        assertEquals(sb.toString(), route.toString());
    }
}