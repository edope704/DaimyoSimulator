package core.app.view;

import core.building.BuildingType;
import core.villager.Role;

import java.util.Map;

public record BuildingViewModel(
        BuildingType type,
        String displayName,
        int timberCost,
        int housingCapacity,
        Map<Role, Integer> jobSlots
) {
    public BuildingViewModel {
        jobSlots = Map.copyOf(jobSlots);
    }
}
