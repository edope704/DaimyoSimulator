package core.placement;

import core.building.Building;
import core.building.BuildingType;
import core.domain.Position;
import core.domain.Village;

public final class MineRequiredRule implements PlacementRule {
    @Override
    public PlacementCheck validate(Village village, Building building, Position position) {
        if (!village.getGrid().hasBuilding(BuildingType.MINE)) {
            return PlacementCheck.fail("Cannot place " + building.getDisplayName() + ": at least one Mine is required");
        }
        return PlacementCheck.ok();
    }
}
