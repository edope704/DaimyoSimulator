package core.placement;

import core.building.Building;
import core.domain.Position;
import core.domain.Village;
import core.resource.ResourceType;
import core.service.ProgressiveCostCalculator;

public final class EnoughTimberRule implements PlacementRule {
    @Override
    public PlacementCheck validate(Village village, Building building, Position position) {
        int existing = (int) village.getGrid().countBuildings(building.getType());
        int cost = ProgressiveCostCalculator.scaledCost(building.getType(), existing, building.getTimberCost());
        if (!village.getResources().has(ResourceType.TIMBER, cost)) {
            return PlacementCheck.fail("Cannot place " + building.getDisplayName() + ": requires "
                    + cost + " timber");
        }
        return PlacementCheck.ok();
    }
}
