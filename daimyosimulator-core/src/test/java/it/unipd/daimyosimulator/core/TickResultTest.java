package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TickResultTest {
    @Test
    void tickResultContainsResourceDeltas() {
        var result = new CoreGameFacade(TestFixtures.config(), TestFixtures.random()).advanceTick();
        assertNotNull(result.producedResources());
        assertNotNull(result.consumedResources());
    }
}
