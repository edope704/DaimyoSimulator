package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.core.service.ConsumptionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsumptionServiceTest {
    @Test
    void shortageReportedWhenResourceMissing() {
        var village = TestFixtures.village();
        village.getResources().set(ResourceType.RICE, 0);
        assertTrue(new ConsumptionService().consume(village).shortages().stream().anyMatch(msg -> msg.contains("RICE")));
    }
}
