package gdx.assets;

import core.policy.PolicyType;
import core.resource.ResourceType;

public final class IconRegistry {
    public String resource(ResourceType type) {
        return switch (type) {
            case RICE -> "icon_resource_rice";
            case TIMBER -> "icon_resource_timber";
            case TOOLS -> "icon_resource_tools";
            case LUXURY_GOODS -> "icon_resource_luxury_goods";
        };
    }

    public String policy(PolicyType type) {
        return switch (type) {
            case AGRICULTURAL_EXPANSION -> "icon_policy_agricultural_expansion";
            case MILITARY_PROTECTION -> "icon_policy_military_protection";
            case CRAFTSMEN_PRODUCTION -> "icon_policy_craftsmen_production";
        };
    }

    public String parameter(ParameterType type) {
        return switch (type) {
            case HAPPINESS -> "icon_parameter_happiness";
            case PROTECTION -> "icon_parameter_protection";
            case FOOD -> "icon_parameter_food";
            case FAITH -> "icon_parameter_faith";
            case HOUSING -> "icon_parameter_housing";
            case CRAFTSMANSHIP -> "icon_parameter_craftsmanship";
            case POPULATION -> "icon_population";
            case EVENT_ALERT -> "icon_event_alert";
        };
    }
}
