package it.unipd.daimyosimulator.core.placement;

import it.unipd.daimyosimulator.core.building.Building;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.resource.ResourceType;

public final class EnoughTimberRule implements PlacementRule {
    @Override
    public PlacementCheck validate(Village village, Building building, Position position) {
        if (!village.getResources().has(ResourceType.TIMBER, building.getTimberCost())) {
            return PlacementCheck.fail("Cannot place " + building.getDisplayName() + ": requires "
                    + building.getTimberCost() + " timber");
        }
        return PlacementCheck.ok();
    }
}
