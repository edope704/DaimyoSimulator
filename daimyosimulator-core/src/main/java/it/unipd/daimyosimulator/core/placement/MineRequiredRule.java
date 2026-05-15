package it.unipd.daimyosimulator.core.placement;

import it.unipd.daimyosimulator.core.building.Building;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.domain.Village;

public final class MineRequiredRule implements PlacementRule {
    @Override
    public PlacementCheck validate(Village village, Building building, Position position) {
        if (!village.getGrid().hasBuilding(BuildingType.MINE)) {
            return PlacementCheck.fail("Cannot place " + building.getDisplayName() + ": at least one Mine is required");
        }
        return PlacementCheck.ok();
    }
}
