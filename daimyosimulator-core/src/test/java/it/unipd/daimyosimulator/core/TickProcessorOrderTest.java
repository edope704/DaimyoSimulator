package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TickProcessorOrderTest {
    @Test
    void tickIncrementsBeforeResult() {
        CoreGameFacade facade = new CoreGameFacade(TestFixtures.config(), TestFixtures.random());
        long before = facade.getCurrentSnapshot().tick();
        var result = facade.advanceTick();
        assertEquals(before + 1, result.afterState().tick());
    }
}
