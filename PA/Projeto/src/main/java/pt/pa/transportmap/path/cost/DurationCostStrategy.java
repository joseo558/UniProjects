package pt.pa.transportmap.path.cost;

import pt.pa.transportmap.path.PathCriteria;
import pt.pa.transportmap.RouteInfo;
import java.util.EnumSet;

/**
 * Cost strategy for PathCriteria Duration
 */
public class DurationCostStrategy implements CostStrategy {
    /** The PathCriteria used */
    private static final PathCriteria pathCriteria = PathCriteria.DURATION;
    /** Collection of the other PathCriteria */
    private static final EnumSet<PathCriteria> otherCosts = EnumSet.complementOf(EnumSet.of(pathCriteria));

    @Override
    public double getCost(RouteInfo info) {
        return info.getDuration();
    }

    @Override
    public void setCost(RouteInfo info, double value) throws IllegalArgumentException{
        info.setDuration(value);
    }

    @Override
    public PathCriteria getPathCriteria(){
        return pathCriteria;
    }

    @Override
    public EnumSet<PathCriteria> getOtherCosts(){
        return otherCosts;
    }
}
