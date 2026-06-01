package core;

import core.service.ConsumptionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RiceConsumptionServiceTest {
    @Test
    void villagersConsumeRice() {
        var village = TestFixtures.village();
        int before = village.getResources().getRice();
        new ConsumptionService().consume(village);
        assertTrue(village.getResources().getRice() < before);
    }
}
