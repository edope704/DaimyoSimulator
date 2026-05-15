package it.unipd.daimyosimulator.core.app.view;

import it.unipd.daimyosimulator.core.villager.Role;

import java.util.EnumMap;
import java.util.Map;

public record PopulationViewModel(int total, int idle, int unhoused, int employed, Map<Role, Integer> roleCounts) {
    public PopulationViewModel {
        EnumMap<Role, Integer> copy = new EnumMap<>(Role.class);
        for (Role role : Role.values()) {
            copy.put(role, roleCounts.getOrDefault(role, 0));
        }
        roleCounts = Map.copyOf(copy);
    }
}
