package core.placement;

import core.building.Building;
import core.domain.Position;
import core.domain.Village;

import java.util.ArrayList;
import java.util.List;

public final class CompositePlacementValidator {
    private final List<PlacementRule> commonRules = List.of(
            new CellInsideGridRule(),
            new CellEmptyRule(),
            new EnoughTimberRule()
    );

    public PlacementCheck validate(Village village, Building building, Position position) {
        List<PlacementRule> rules = new ArrayList<>(commonRules);
        rules.addAll(building.getPlacementRules());
        for (PlacementRule rule : rules) {
            PlacementCheck check = rule.validate(village, building, position);
            if (!check.success()) {
                return check;
            }
        }
        return PlacementCheck.ok();
    }
}
