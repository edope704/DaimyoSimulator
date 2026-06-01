package core;

import core.app.CoreGameFacade;
import core.building.BuildingType;
import core.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreGameFacadeTest {
    @Test
    void facadePlacesBuildingThroughController() {
        CoreGameFacade facade = new CoreGameFacade(TestFixtures.config(), TestFixtures.random());
        assertTrue(facade.constructBuilding(BuildingType.DWELLING, new Position(0, 0)).success());
    }
}
