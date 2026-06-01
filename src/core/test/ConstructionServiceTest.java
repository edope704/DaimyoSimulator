package core;

import core.building.BuildingType;
import core.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstructionServiceTest {
    @Test
    void validPlacementConsumesTimberAndPlacesBuilding() {
        var village = TestFixtures.village();
        int timberBefore = village.getResources().getTimber();
        var result = TestFixtures.constructionService(village.getConfig())
                .constructBuilding(village, BuildingType.DWELLING, new Position(0, 0));
        assertTrue(result.success());
        assertEquals(timberBefore - 15, village.getResources().getTimber());
        assertTrue(village.getGrid().getCell(new Position(0, 0)).hasBuilding());
    }
}
