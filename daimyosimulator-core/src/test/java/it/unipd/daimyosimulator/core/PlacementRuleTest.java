package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.building.Dwelling;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.placement.CellInsideGridRule;
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
