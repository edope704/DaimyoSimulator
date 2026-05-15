package it.unipd.daimyosimulator.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VillageInitializerTest {
    @Test
    void initializerCreatesConfiguredVillage() {
        var village = TestFixtures.village();
        assertEquals(5, village.getGrid().getWidth());
        assertEquals(5, village.getGrid().getHeight());
        assertEquals(TestFixtures.config().initialVillagers(), village.getVillagers().size());
    }
}
