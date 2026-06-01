package core.app.view;

import core.policy.PolicyType;

import java.util.EnumMap;
import java.util.Map;

public record PolicyViewModel(
        PolicyType activePolicy,
        String activeDisplayName,
        int activeRemainingTicks,
        Map<PolicyType, Integer> cooldowns
) {
    public PolicyViewModel {
        EnumMap<PolicyType, Integer> copy = new EnumMap<>(PolicyType.class);
        for (PolicyType type : PolicyType.values()) {
            copy.put(type, cooldowns.getOrDefault(type, 0));
        }
        cooldowns = Map.copyOf(copy);
    }
}
