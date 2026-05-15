package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.service.ProductionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RicePaddyWithoutFarmerTest {
    @Test
    void paddyWithoutFarmerProducesNoRice() {
        var village = TestFixtures.village();
        TestFixtures.place(village, BuildingType.RICE_FARM, 0, 0);
        TestFixtures.place(village, BuildingType.RICE_PADDY, 1, 0);
        assertEquals(0, new ProductionService().produce(village).getRice());
    }
}
