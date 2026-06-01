package core.policy;

import core.villager.Role;

public final class MilitaryProtectionPolicy implements PolicyStrategy {
    @Override
    public PolicyType getType() {
        return PolicyType.MILITARY_PROTECTION;
    }

    @Override
    public String getDisplayName() {
        return "Military Protection";
    }

    @Override
    public double toolsConsumptionMultiplier(Role role) {
        return role == Role.SAMURAI ? 1.5 : 1.0;
    }

    @Override
    public double luxuryConsumptionMultiplier(Role role) {
        return role == Role.SAMURAI ? 1.5 : 1.0;
    }

    @Override
    public double protectionMultiplier() {
        return 1.5;
    }
}
