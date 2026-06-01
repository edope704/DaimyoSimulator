package core.policy;

import core.config.GameConfig;
import core.factory.PolicyFactory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class PolicyManager {
    private final PolicyFactory factory;
    private final EnumMap<PolicyType, Integer> cooldowns = new EnumMap<>(PolicyType.class);
    private PolicyStrategy activePolicy = new NoPolicy();
    private int activeRemainingTicks;

    public PolicyManager(PolicyFactory factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
        for (PolicyType type : PolicyType.values()) {
            cooldowns.put(type, 0);
        }
    }

    public PolicyActivation activate(PolicyType type, GameConfig config) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(config, "config");
        if (isPolicyActive()) {
            return new PolicyActivation(false, "Policy already active: " + activePolicy.getDisplayName());
        }
        int cooldown = cooldowns.getOrDefault(type, 0);
        if (cooldown > 0) {
            return new PolicyActivation(false, type + " is on cooldown for " + cooldown + " ticks");
        }
        activePolicy = factory.create(type);
        activeRemainingTicks = activePolicy.getDurationTicks(config);
        return new PolicyActivation(true, activePolicy.getDisplayName() + " activated for " + activeRemainingTicks + " ticks");
    }

    public List<String> advanceTick(GameConfig config) {
        List<String> messages = new ArrayList<>();
        for (PolicyType type : PolicyType.values()) {
            int current = cooldowns.getOrDefault(type, 0);
            if (current > 0) {
                cooldowns.put(type, current - 1);
            }
        }
        if (isPolicyActive()) {
            activeRemainingTicks--;
            if (activeRemainingTicks <= 0) {
                PolicyType endedType = activePolicy.getType();
                int cooldown = activePolicy.getCooldownTicks(config);
                cooldowns.put(endedType, cooldown);
                messages.add(activePolicy.getDisplayName() + " expired; cooldown " + cooldown + " ticks");
                activePolicy = new NoPolicy();
                activeRemainingTicks = 0;
            }
        }
        return messages;
    }

    public boolean isPolicyActive() {
        return activePolicy.getType() != null;
    }

    public PolicyStrategy getActivePolicy() {
        return activePolicy;
    }

    public Optional<PolicyType> getActiveType() {
        return Optional.ofNullable(activePolicy.getType());
    }

    public int getActiveRemainingTicks() {
        return activeRemainingTicks;
    }

    public Map<PolicyType, Integer> getCooldowns() {
        return Map.copyOf(cooldowns);
    }

    public int getCooldown(PolicyType type) {
        return cooldowns.getOrDefault(type, 0);
    }

    public void restore(PolicyType activeType, int activeRemainingTicks, Map<PolicyType, Integer> restoredCooldowns) {
        cooldowns.clear();
        for (PolicyType type : PolicyType.values()) {
            cooldowns.put(type, Math.max(0, restoredCooldowns.getOrDefault(type, 0)));
        }
        if (activeType == null || activeRemainingTicks <= 0) {
            activePolicy = new NoPolicy();
            this.activeRemainingTicks = 0;
        } else {
            activePolicy = factory.create(activeType);
            this.activeRemainingTicks = activeRemainingTicks;
        }
    }
}
