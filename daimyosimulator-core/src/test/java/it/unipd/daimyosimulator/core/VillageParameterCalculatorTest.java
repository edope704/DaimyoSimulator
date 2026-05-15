package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.service.HappinessCalculator;
import it.unipd.daimyosimulator.core.service.HousingService;
import it.unipd.daimyosimulator.core.service.VillageParameterCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class VillageParameterCalculatorTest {
    @Test
    void calculatorKeepsValuesInRange() {
        var village = TestFixtures.village();
        new VillageParameterCalculator(new HousingService(), new HappinessCalculator()).recalculate(village);
        assertTrue(village.getParameters().getFood() >= 0 && village.getParameters().getFood() <= 100);
        assertTrue(village.getParameters().getHappiness() >= 0 && village.getParameters().getHappiness() <= 100);
    }
}
