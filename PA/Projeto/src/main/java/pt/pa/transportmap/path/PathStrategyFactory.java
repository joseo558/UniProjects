package pt.pa.transportmap.path;

import pt.pa.transportmap.path.cost.DistanceCostStrategy;
import pt.pa.transportmap.path.cost.DurationCostStrategy;
import pt.pa.transportmap.path.cost.SustainabilityCostStrategy;

/**
 * Simple Factory to create a PathStrategy
 */
public class PathStrategyFactory {
    /**
     * Create a PathStrategy for a given PathCriteria
     * @param criteria PathCriteria the criteria to use
     * @return PathStrategy a path strategy optimized for the given criteria
     * @throws IllegalStateException if criteria is null or not implemented
     * @throws IllegalArgumentException if criteria is invalid (Java Enum constant check)
     */
    public static PathStrategy create(PathCriteria criteria) throws IllegalStateException, IllegalArgumentException {
        // java throws IllegalArgumentException if criteria is invalid
        if(criteria == null){
            throw new IllegalStateException("PathCriteria is null.");
        }
        switch(criteria) {
            case DISTANCE:
                return new DijkstraStrategy(new DistanceCostStrategy());
                // or DFSStrategy
            case DURATION:
                return new DijkstraStrategy(new DurationCostStrategy());
            case SUSTAINABILITY:
                return new BellmanStrategy(new SustainabilityCostStrategy());
                // or DijkstraNegativeFixStrategy
            default:
                throw new IllegalStateException("PathCriteria is not implemented.");
        }
    }
}
