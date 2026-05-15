package it.unipd.daimyosimulator.core.building;

import it.unipd.daimyosimulator.core.placement.PlacementRule;
import it.unipd.daimyosimulator.core.villager.Role;

import java.util.List;
import java.util.Map;

public interface Building {
    BuildingType getType();

    int getTimberCost();

    String getDisplayName();

    default int getHousingCapacity() {
        return 0;
    }

    default Map<Role, Integer> getJobSlots() {
        return Map.of();
    }

    default List<PlacementRule> getPlacementRules() {
        return List.of();
    }
}
