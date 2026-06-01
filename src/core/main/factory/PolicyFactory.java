package core.factory;

import core.policy.*;

import java.util.Objects;

public final class PolicyFactory {
    public PolicyStrategy create(PolicyType type) {
        return switch (Objects.requireNonNull(type, "type")) {
            case AGRICULTURAL_EXPANSION -> new AgriculturalExpansionPolicy();
            case MILITARY_PROTECTION -> new MilitaryProtectionPolicy();
            case CRAFTSMEN_PRODUCTION -> new CraftsmenProductionPolicy();
        };
    }
}
