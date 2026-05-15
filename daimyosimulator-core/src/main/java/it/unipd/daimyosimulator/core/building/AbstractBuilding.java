package it.unipd.daimyosimulator.core.building;

import it.unipd.daimyosimulator.core.placement.PlacementRule;
import it.unipd.daimyosimulator.core.villager.Role;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractBuilding implements Building {
    private final BuildingType type;
    private final int timberCost;
    private final String displayName;
    private final int housingCapacity;
    private final Map<Role, Integer> jobSlots;
    private final List<PlacementRule> placementRules;

    protected AbstractBuilding(
            BuildingType type,
            int timberCost,
            String displayName,
            int housingCapacity,
            Map<Role, Integer> jobSlots,
            List<PlacementRule> placementRules
    ) {
        if (timberCost < 0) {
            throw new IllegalArgumentException("Timber cost cannot be negative");
        }
        this.type = Objects.requireNonNull(type, "type");
        this.timberCost = timberCost;
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.housingCapacity = housingCapacity;
        this.jobSlots = Map.copyOf(jobSlots);
        this.placementRules = List.copyOf(placementRules);
    }

    @Override
    public BuildingType getType() {
        return type;
    }

    @Override
    public int getTimberCost() {
        return timberCost;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int getHousingCapacity() {
        return housingCapacity;
    }

    @Override
    public Map<Role, Integer> getJobSlots() {
        return jobSlots;
    }

    @Override
    public List<PlacementRule> getPlacementRules() {
        return placementRules;
    }
}
