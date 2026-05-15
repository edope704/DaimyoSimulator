package it.unipd.daimyosimulator.core.placement;

import it.unipd.daimyosimulator.core.building.Building;
import it.unipd.daimyosimulator.core.domain.Cell;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.domain.Village;

public final class CellEmptyRule implements PlacementRule {
    @Override
    public PlacementCheck validate(Village village, Building building, Position position) {
        if (!village.getGrid().isInside(position)) {
            return PlacementCheck.ok();
        }
        Cell cell = village.getGrid().getCell(position);
        if (!cell.isEmpty()) {
            return PlacementCheck.fail("Cannot place " + building.getDisplayName() + ": cell " + position + " is occupied");
        }
        return PlacementCheck.ok();
    }
}
