package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MineRequiredRuleTest {
    @Test
    void smithyAndWorkshopRequireMine() {
        var village = TestFixtures.village();
        var service = TestFixtures.constructionService(village.getConfig());
        assertFalse(service.constructBuilding(village, BuildingType.SMITHY, new Position(0, 0)).success());
        assertFalse(service.constructBuilding(village, BuildingType.WORKSHOP, new Position(1, 0)).success());
        assertTrue(service.constructBuilding(village, BuildingType.MINE, new Position(0, 0)).success());
        assertTrue(service.constructBuilding(village, BuildingType.SMITHY, new Position(1, 0)).success());
    }
}
