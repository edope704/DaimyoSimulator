package it.unipd.daimyosimulator.core.policy;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.config.GameConfig;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.core.villager.Role;

public interface PolicyStrategy {
    PolicyType getType();

    String getDisplayName();

    default int getDurationTicks(GameConfig config) {
        return config.policyDurationTicks();
    }

    default int getCooldownTicks(GameConfig config) {
        return config.policyCooldownTicks();
    }

    default double productionMultiplier(ResourceType resourceType, BuildingType buildingType) {
        return 1.0;
    }

    default double riceConsumptionMultiplier(Role role) {
        return 1.0;
    }

    default double toolsConsumptionMultiplier(Role role) {
        return 1.0;
    }

    default double luxuryConsumptionMultiplier(Role role) {
        return 1.0;
    }

    default double protectionMultiplier() {
        return 1.0;
    }
}
