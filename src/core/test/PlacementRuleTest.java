package core;

import core.building.Dwelling;
import core.domain.Position;
import core.placement.CellInsideGridRule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class PlacementRuleTest {
    @Test
    void insideGridRuleRejectsOutside() {
        var village = TestFixtures.village();
        var check = new CellInsideGridRule().validate(village, new Dwelling(), new Position(99, 0));
        assertFalse(check.success());
    }
}
