package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WoodcutterNearForestRuleTest {
    @Test
    void woodcutterRequiresNearbyForest() {
        var village = TestFixtures.village();
        var service = TestFixtures.constructionService(village.getConfig());
        assertFalse(service.constructBuilding(village, BuildingType.WOODCUTTERS_HUT, new Position(0, 0)).success());
        assertTrue(service.constructBuilding(village, BuildingType.WOODCUTTERS_HUT, new Position(3, 4)).success());
    }
}
