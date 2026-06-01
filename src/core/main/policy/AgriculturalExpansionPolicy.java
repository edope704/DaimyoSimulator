package core.policy;

import core.building.BuildingType;
import core.resource.ResourceType;
import core.villager.Role;

public final class AgriculturalExpansionPolicy implements PolicyStrategy {
    @Override
    public PolicyType getType() {
        return PolicyType.AGRICULTURAL_EXPANSION;
    }

    @Override
    public String getDisplayName() {
        return "Agricultural Expansion";
    }

    @Override
    public double productionMultiplier(ResourceType resourceType, BuildingType buildingType) {
        if (resourceType == ResourceType.RICE && buildingType == BuildingType.RICE_PADDY) {
            return 1.5;
        }
        return 1.0;
    }

    @Override
    public double toolsConsumptionMultiplier(Role role) {
        return role == Role.RICE_FARMER ? 1.5 : 1.0;
    }
}
