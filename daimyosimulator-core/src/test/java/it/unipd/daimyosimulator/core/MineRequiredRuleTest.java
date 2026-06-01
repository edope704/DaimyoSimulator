package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smithy and Workshop now use proximity-based functionality (like the Paddy-Farm rule).
 * Placement is allowed anywhere; production only occurs when a Mine is adjacent.
 */
class MineRequiredRuleTest {
    @Test
    void smithyAndWorkshopCanBePlacedWithoutMine() {
        var village = TestFixtures.village();
        var service = TestFixtures.constructionService(village.getConfig());
        // Placement succeeds even without a mine — proximity check is for production, not placement
        assertTrue(service.constructBuilding(village, BuildingType.SMITHY, new Position(0, 0)).success());
        assertTrue(service.constructBuilding(village, BuildingType.WORKSHOP, new Position(1, 0)).success());
    }

    @Test
    void smithyAndWorkshopCanAlsoBeBuiltNearMine() {
        var village = TestFixtures.village();
        var service = TestFixtures.constructionService(village.getConfig());
        assertTrue(service.constructBuilding(village, BuildingType.MINE, new Position(0, 0)).success());
        assertTrue(service.constructBuilding(village, BuildingType.SMITHY, new Position(1, 0)).success());
    }
}
