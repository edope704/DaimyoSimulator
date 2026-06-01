package core;

import core.building.BuildingType;
import core.service.HousingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HousingServiceTest {
    @Test
    void dwellingAddsHousingCapacity() {
        var village = TestFixtures.village();
        TestFixtures.place(village, BuildingType.DWELLING, 0, 0);
        assertTrue(new HousingService().housingCapacity(village) >= 4);
    }
}
