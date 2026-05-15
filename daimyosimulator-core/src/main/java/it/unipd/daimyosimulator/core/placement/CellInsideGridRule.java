package it.unipd.daimyosimulator.core.placement;

import it.unipd.daimyosimulator.core.building.Building;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.domain.Village;

public final class CellInsideGridRule implements PlacementRule {
    @Override
    public PlacementCheck validate(Village village, Building building, Position position) {
        if (!village.getGrid().isInside(position)) {
            return PlacementCheck.fail("Cannot place " + building.getDisplayName() + ": " + position + " is outside the grid");
        }
        return PlacementCheck.ok();
    }
}
