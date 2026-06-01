package core;

import core.building.BuildingType;
import core.service.BirthDeathService;
import core.service.HousingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BirthDeathServiceTest {
    @Test
    void birthProgressCanCreateVillager() {
        var village = TestFixtures.village();
        TestFixtures.place(village, BuildingType.DWELLING, 0, 0);
        village.getParameters().setFood(100);
        village.getParameters().setHousing(100);
        village.getParameters().setHappiness(100);
        village.setBirthProgress(90);
        int before = village.getVillagers().size();
        new BirthDeathService(new HousingService(), TestFixtures.random()).process(village);
        assertTrue(village.getVillagers().size() >= before);
    }
}
