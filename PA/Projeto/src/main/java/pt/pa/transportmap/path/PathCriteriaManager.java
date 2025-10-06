package pt.pa.transportmap.path;

/**
 * Manages the global PathCriteria value to be shared across different views.
 * Provides static methods to set and retrieve the current PathCriteria.
 */
public class PathCriteriaManager {

    private static PathCriteria pathCriteria;

    /**
     * Retrieves the current PathCriteria value.
     *
     * @return the current PathCriteria.
     */
    public static PathCriteria getPathCriteria() {
        return pathCriteria;
    }

    /**
     * Updates the global PathCriteria value.
     *
     * @param criteria the new PathCriteria to set.
     */
    public static void setPathCriteria(PathCriteria criteria) {
        pathCriteria = criteria;
    }
}

