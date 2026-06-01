package core.policy;

import core.building.BuildingType;
import core.config.GameConfig;
import core.resource.ResourceType;
import core.villager.Role;

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
