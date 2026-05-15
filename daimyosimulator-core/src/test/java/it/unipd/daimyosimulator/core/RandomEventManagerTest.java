package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.event.RandomEventManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class RandomEventManagerTest {
    @Test
    void disabledEventsProduceNoEvents() {
        var village = TestFixtures.village();
        assertFalse(village.getConfig().randomEventsEnabled());
        assertFalse(new RandomEventManager(TestFixtures.random()).evaluate(village).iterator().hasNext());
    }
}
