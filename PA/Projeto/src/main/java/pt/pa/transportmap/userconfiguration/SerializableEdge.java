package pt.pa.transportmap.userconfiguration;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import pt.pa.transportmap.Route;
import pt.pa.transportmap.Stop;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an edge in a graph that is serializable
 */
public class SerializableEdge implements Serializable {
    /** Serial version UID */
    @Serial
    private static final long serialVersionUID = 1L;
    /** Source stop */
    private Stop source;
    /** Destination stop */
    private Stop destination;

    /**
     * Constructor for SerializableEdge
     * @param edge Edge<Route, Stop> the edge to be serialized
     */
    public SerializableEdge(Edge<Route, Stop> edge) {
        Vertex<Stop>[] edgeVertices = edge.vertices();
        this.source = edgeVertices[0].element();
        this.destination = edgeVertices[1].element();
    }

    /**
     * Get the source stop
     * @return Stop the source stop
     */
    public Stop getSource() {
        return source;
    }

    /**
     * Get the destination stop
     * @return Stop the destination stop
     */
    public Stop getDestination() {
        return destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerializableEdge edge = (SerializableEdge) o;
        return Objects.equals(source, edge.source) && Objects.equals(destination, edge.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination);
    }
}