package it.unipd.daimyosimulator.core.placement;

import it.unipd.daimyosimulator.core.building.Building;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.core.service.ProgressiveCostCalculator;

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
