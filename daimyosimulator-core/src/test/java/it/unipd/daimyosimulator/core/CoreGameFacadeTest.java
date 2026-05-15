package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreGameFacadeTest {
    @Test
    void facadePlacesBuildingThroughController() {
        CoreGameFacade facade = new CoreGameFacade(TestFixtures.config(), TestFixtures.random());
        assertTrue(facade.constructBuilding(BuildingType.DWELLING, new Position(0, 0)).success());
    }
}
