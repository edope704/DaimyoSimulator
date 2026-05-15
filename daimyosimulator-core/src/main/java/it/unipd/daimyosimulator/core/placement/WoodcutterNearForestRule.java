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
        if (!village.getGrid().hasNaturalFeatureWithin(position, NaturalFeature.FOREST, adjacencyRange)) {
            return PlacementCheck.fail("Cannot place Woodcutter's Hut: must be near forest");
        }
        return PlacementCheck.ok();
    }
}
