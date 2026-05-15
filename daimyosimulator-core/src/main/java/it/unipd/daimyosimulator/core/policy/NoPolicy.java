package it.unipd.daimyosimulator.core.policy;

public final class NoPolicy implements PolicyStrategy {
    @Override
    public PolicyType getType() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "No active policy";
    }
}
