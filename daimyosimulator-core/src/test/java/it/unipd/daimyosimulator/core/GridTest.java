package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.domain.Grid;
import it.unipd.daimyosimulator.core.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {
    @Test
    void gridRejectsOutsideCoordinates() {
        Grid grid = new Grid(3, 2);
        assertTrue(grid.isInside(new Position(2, 1)));
        assertFalse(grid.isInside(new Position(3, 1)));
        assertThrows(IllegalArgumentException.class, () -> grid.getCell(new Position(-1, 0)));
    }
}
