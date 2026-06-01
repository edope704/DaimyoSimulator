package core;

import core.building.BuildingType;
import core.domain.Position;
import core.service.ProductionService;
import core.villager.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RiceProductionServiceTest {
    @Test
    void riceProductionAddsRice() {
        var village = TestFixtures.village();
        TestFixtures.place(village, BuildingType.RICE_FARM, 0, 0);
        TestFixtures.place(village, BuildingType.RICE_PADDY, 1, 0);
        village.getVillagers().get(0).houseAt(new Position(0, 0));
        village.getVillagers().get(0).assignRole(Role.RICE_FARMER);
        int before = village.getResources().getRice();
        new ProductionService().produce(village);
        assertTrue(village.getResources().getRice() > before);
    }
}
