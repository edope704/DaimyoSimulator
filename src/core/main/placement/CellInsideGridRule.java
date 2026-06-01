package core.placement;

import core.building.Building;
import core.domain.Position;
import core.domain.Village;

public final class CellInsideGridRule implements PlacementRule {
    @Override
    public PlacementCheck validate(Village village, Building building, Position position) {
        if (!village.getGrid().isInside(position)) {
            return PlacementCheck.fail("Cannot place " + building.getDisplayName() + ": " + position + " is outside the grid");
        }
        return PlacementCheck.ok();
    }
}
