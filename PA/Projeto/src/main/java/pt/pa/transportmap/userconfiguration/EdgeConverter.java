package pt.pa.transportmap.userconfiguration;

import com.brunomnsilva.smartgraph.graph.Edge;
import pt.pa.transportmap.Route;
import pt.pa.transportmap.Stop;
import pt.pa.transportmap.TransportMap;

/**
 * Converts an edge from the user configuration to the model
 */
public class EdgeConverter {
    /** The model */
    private final TransportMap model;

    /**
     * Creates a new edge converter
     * @param model TransportMap the model
     */
    public EdgeConverter(TransportMap model) {
        this.model = model;
    }

    /**
     * Converts an edge from the user configuration to the model
     * @param serializableEdge SerializableEdge the edge
     * @return Edge<Route, Stop> the edge or null if the serializableEdge is null
     */
    public Edge<Route, Stop> getEdge(SerializableEdge serializableEdge) {
        if( serializableEdge == null ) {
            return null;
        }
        return model.getEdge( model.getVertex(serializableEdge.getSource()), model.getVertex(serializableEdge.getDestination()) );
    }
}
