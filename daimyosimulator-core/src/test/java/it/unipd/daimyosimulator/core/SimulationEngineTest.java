package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationEngineTest {
    @Test
    void engineReturnsBeforeAndAfterSnapshots() {
        CoreGameFacade facade = new CoreGameFacade(TestFixtures.config(), TestFixtures.random());
        var result = facade.advanceTick();
        assertTrue(result.afterState().tick() > result.beforeState().tick());
    }
}
