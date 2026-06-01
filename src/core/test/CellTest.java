package core;

import core.building.Dwelling;
import core.domain.Cell;
import core.domain.NaturalFeature;
import core.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {
    @Test
    void cellContainsOneFeatureOrBuilding() {
        Cell cell = new Cell(new Position(0, 0));
        cell.setNaturalFeature(NaturalFeature.FOREST);
        assertTrue(cell.hasNaturalFeature());
        assertThrows(IllegalStateException.class, () -> cell.setBuilding(new Dwelling()));
    }
}
