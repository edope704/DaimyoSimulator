package core;

import core.domain.NaturalFeature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ForestGenerationTest {
    @Test
    void initializerEnsuresAtLeastOneForest() {
        var village = TestFixtures.village();
        assertTrue(village.getGrid().getCells().stream()
                .anyMatch(cell -> cell.getNaturalFeature().filter(NaturalFeature.FOREST::equals).isPresent()));
    }
}
