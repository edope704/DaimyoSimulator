package it.unipd.daimyosimulator.gdx.assets;

import it.unipd.daimyosimulator.core.domain.NaturalFeature;
import it.unipd.daimyosimulator.core.policy.PolicyType;
import it.unipd.daimyosimulator.core.resource.ResourceType;

public final class IconRegistry {
    public String naturalFeature(NaturalFeature feature) {
        return feature == NaturalFeature.FOREST ? "feature_forest" : MissingAssetFallback.NAME;
    }

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
}
