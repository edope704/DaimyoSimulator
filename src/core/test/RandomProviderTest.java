package core;

import core.random.FixedRandomProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomProviderTest {
    @Test
    void fixedProviderIsDeterministic() {
        FixedRandomProvider provider = new FixedRandomProvider(0.25, 7);
        assertEquals(2, provider.nextInt(5));
        assertTrue(provider.chance(0.5));
        assertFalse(provider.chance(0.1));
    }
}
