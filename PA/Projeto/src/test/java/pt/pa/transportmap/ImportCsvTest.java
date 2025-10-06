package pt.pa.transportmap;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pa.view.Coordinate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ImportCsv class
 */
class ImportCsvTest {
    /** Model entity */
    TransportMap model;

    @BeforeEach
    void setUp() {
        Graph<Stop, Route> graph = new GraphEdgeList<>();
        this.model = new TransportMap(graph);
        ImportCsv.update(this.model);
    }

    @Test
    void update() {
        assertEquals(31, model.getNumberOfStops());
        assertEquals(39, model.getNumberOfRoutes());
        ImportCsv.update(this.model); // next updates should not add any new vertices or edges (files have same)
        assertDoesNotThrow(model::update);
        assertEquals(31, model.getNumberOfStops());
        assertEquals(39, model.getNumberOfRoutes());

        assertTrue(model.hasStop("PAL018"));
        assertTrue(model.hasStop("COI017"));

        assertTrue(model.areAdjacent("PAL018", "COI017"));
        assertTrue(model.areAdjacent("CAIS019", "ALG011"));
    }

    @Test
    void readCoords() {
        Map<String, Coordinate> map = ImportCsv.readCoords();
        assertEquals(31, map.size());
        assertEquals(858, map.get("SET014").getPosX());
        assertEquals(562, map.get("SET014").getPosY());

        assertEquals(559, map.get("SEI024").getPosX());
        assertEquals(353, map.get("SEI024").getPosY());
    }
}