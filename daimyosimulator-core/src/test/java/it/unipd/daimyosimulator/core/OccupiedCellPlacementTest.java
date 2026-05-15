package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OccupiedCellPlacementTest {
    @Test
    void occupiedCellRejectedWithoutTimberLoss() {
        var village = TestFixtures.village();
        TestFixtures.place(village, BuildingType.DWELLING, 0, 0);
        int timberBefore = village.getResources().getTimber();
        var result = TestFixtures.constructionService(village.getConfig())
                .constructBuilding(village, BuildingType.MARKET, new Position(0, 0));
        assertFalse(result.success());
        assertEquals(timberBefore, village.getResources().getTimber());
    }
}
