package it.unipd.daimyosimulator.core.building;

import it.unipd.daimyosimulator.core.placement.WoodcutterNearForestRule;
import it.unipd.daimyosimulator.core.villager.Role;

import java.util.List;
import java.util.Map;

public final class WoodcuttersHut extends AbstractBuilding {
    public WoodcuttersHut(int adjacencyRange) {
        super(BuildingType.WOODCUTTERS_HUT, 20, "Woodcutter's Hut", 0,
                Map.of(Role.WOODCUTTER, 3), List.of(new WoodcutterNearForestRule(adjacencyRange)));
    }
}
