package core;

import core.service.HappinessCalculator;
import core.service.HousingService;
import core.service.VillageParameterCalculator;
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
