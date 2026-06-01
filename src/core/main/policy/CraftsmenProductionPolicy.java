package core.policy;

import core.building.BuildingType;
import core.resource.ResourceType;
import core.villager.Role;

public final class CraftsmenProductionPolicy implements PolicyStrategy {
    @Override
    public PolicyType getType() {
        return PolicyType.CRAFTSMEN_PRODUCTION;
    }

    @Override
    public String getDisplayName() {
        return "Craftsmen Production";
    }

    @Override
    public double productionMultiplier(ResourceType resourceType, BuildingType buildingType) {
        return switch (resourceType) {
            case TIMBER, TOOLS, LUXURY_GOODS -> 1.5;
            case RICE -> 1.0;
        };
    }

    @Override
    public double riceConsumptionMultiplier(Role role) {
        return role == Role.BLACKSMITH || role == Role.ARTISAN ? 1.5 : 1.0;
    }
}
