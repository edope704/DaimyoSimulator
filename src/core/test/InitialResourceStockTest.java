package core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InitialResourceStockTest {
    @Test
    void initialResourcesMatchConfig() {
        var village = TestFixtures.village();
        assertEquals(100, village.getResources().getRice());
        assertEquals(500, village.getResources().getTimber());
        assertEquals(50, village.getResources().getTools());
        assertEquals(30, village.getResources().getLuxuryGoods());
    }
}
