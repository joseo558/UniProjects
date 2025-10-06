package pt.pa.path;

import org.junit.jupiter.api.Test;
import pt.pa.transportmap.path.PathCriteria;
import pt.pa.transportmap.path.PathStrategyFactory;

import static org.junit.jupiter.api.Assertions.*;

class PathStrategyFactoryTest {

    @Test
    void create() {
        assertThrows(IllegalStateException.class, () -> PathStrategyFactory.create(null));
        // java throws for invalid PathCriteria
        assertThrows(IllegalArgumentException.class, () -> PathStrategyFactory.create(PathCriteria.valueOf("Algo")));

        // is a simple switch, doesn't need to be tested
        // class may be modified frequently which would break tests
    }
}