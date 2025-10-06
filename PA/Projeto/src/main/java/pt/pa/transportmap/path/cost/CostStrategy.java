package pt.pa.transportmap.path.cost;

import pt.pa.transportmap.path.PathCriteria;
import pt.pa.transportmap.RouteInfo;
import java.util.EnumSet;

/**
 * Define a cost strategy to get and set path costs based on PathCriteria Enum
 * Each concrete strategy must store a static constant of the PathCriteria used and a collection of the others
 */
public interface CostStrategy {
    /**
     * Set a RouteInfo cost
     * @param info RouteInfo a route info object with costs to update
     * @param value double the new value
     * @throws IllegalArgumentException if the value is invalid
     */
    void setCost(RouteInfo info, double value) throws IllegalArgumentException;

    /**
     * Return the RouteInfo cost
     * @param info RouteInfo a route info object with costs
     * @return double the cost in the RouteInfo object
     */
    double getCost(RouteInfo info);

    /**
     * Get the concrete strategy path criteria
     * @return PathCriteria the concrete strategy path criteria
     */
    PathCriteria getPathCriteria();

    /**
     * Get a collection of the other PathCriteria
     * @return EnumSet<PathCriteria> a collection of the other PathCriteria
     */
    EnumSet<PathCriteria> getOtherCosts();
}
