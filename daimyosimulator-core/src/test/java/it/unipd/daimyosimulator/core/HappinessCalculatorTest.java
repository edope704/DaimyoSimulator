package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.domain.VillageParameters;
import it.unipd.daimyosimulator.core.service.HappinessCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HappinessCalculatorTest {
    @Test
    void happinessUsesWeightedAverage() {
        int value = new HappinessCalculator().calculate(new VillageParameters(0, 100, 100, 100, 100, 100));
        assertEquals(100, value);
    }
}
