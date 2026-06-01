package core;

import core.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PositionTest {
    @Test
    void positionUsesValueEqualityAndNeighbors() {
        assertEquals(new Position(1, 2), new Position(1, 2));
        assertEquals(8, new Position(0, 0).neighbors(1).size());
        assertEquals(2, new Position(0, 0).chebyshevDistance(new Position(2, -1)));
    }
}
