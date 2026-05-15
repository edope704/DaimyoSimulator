package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.service.ProductionService;
import it.unipd.daimyosimulator.core.villager.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductionServiceTest {
    @Test
    void smithyWithBlacksmithProducesToolsWhenMineExists() {
        var village = TestFixtures.village();
        TestFixtures.place(village, BuildingType.MINE, 0, 0);
        TestFixtures.place(village, BuildingType.SMITHY, 1, 0);
        village.getVillagers().get(0).houseAt(new Position(0, 0));
        village.getVillagers().get(0).assignRole(Role.BLACKSMITH);
        assertTrue(new ProductionService().produce(village).getTools() > 0);
    }
}
