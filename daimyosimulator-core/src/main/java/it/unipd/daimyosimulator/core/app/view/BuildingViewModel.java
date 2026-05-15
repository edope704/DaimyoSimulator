package it.unipd.daimyosimulator.core.app.view;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.villager.Role;

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
