package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.core.service.BirthDeathService;
import it.unipd.daimyosimulator.core.service.HousingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StarvationDeathTest {
    @Test
    void starvationKillsAfterConfiguredInterval() {
        var village = TestFixtures.village();
        village.getResources().set(ResourceType.RICE, 0);
        int before = village.getVillagers().size();
        BirthDeathService service = new BirthDeathService(new HousingService(), TestFixtures.random());
        service.process(village);
        service.process(village);
        service.process(village);
        assertEquals(before - 1, village.getVillagers().size());
    }
}
