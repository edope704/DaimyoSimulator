package core;

import core.domain.VillageParameters;
import core.service.HappinessCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HappinessCalculatorTest {
    @Test
    void happinessUsesWeightedAverage() {
        int value = new HappinessCalculator().calculate(new VillageParameters(0, 100, 100, 100, 100, 100));
        assertEquals(100, value);
    }
}
