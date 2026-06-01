package it.unipd.daimyosimulator.core.placement;

import it.unipd.daimyosimulator.core.building.Building;
import it.unipd.daimyosimulator.core.domain.NaturalFeature;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.domain.Village;

public final class WoodcutterNearForestRule implements PlacementRule {
    private final int adjacencyRange;

    public WoodcutterNearForestRule(int adjacencyRange) {
        if (adjacencyRange <= 0) {
            throw new IllegalArgumentException("Adjacency range must be positive");
        }
        this.adjacencyRange = adjacencyRange;
    }

    @Override
    public PlacementCheck validate(Village village, Building building, Position position) {
        if (!village.getGrid().isInside(position)) {
            return PlacementCheck.ok();
        }
        // Valid if adjacent to a forest NaturalFeature within the playable grid.
        if (village.getGrid().hasNaturalFeatureWithin(position, NaturalFeature.FOREST, adjacencyRange)) {
            return PlacementCheck.ok();
        }
        // Also valid if within adjacencyRange of any grid edge, meaning the hut can
        // reach the non-playable outer tree border that surrounds the map visually.
        if (isNearGridEdge(position, village.getConfig().gridWidth(), village.getConfig().gridHeight())) {
            return PlacementCheck.ok();
        }
        return PlacementCheck.fail("Cannot place Woodcutter's Hut: must be near a forest or the outer tree border");
    }

    private boolean isNearGridEdge(Position pos, int gridWidth, int gridHeight) {
        return pos.x() < adjacencyRange
                || pos.y() < adjacencyRange
                || pos.x() >= gridWidth  - adjacencyRange
                || pos.y() >= gridHeight - adjacencyRange;
    }
}
