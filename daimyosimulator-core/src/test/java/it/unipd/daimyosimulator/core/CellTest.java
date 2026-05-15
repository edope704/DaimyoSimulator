package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.building.Dwelling;
import it.unipd.daimyosimulator.core.domain.Cell;
import it.unipd.daimyosimulator.core.domain.NaturalFeature;
import it.unipd.daimyosimulator.core.domain.Position;
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
