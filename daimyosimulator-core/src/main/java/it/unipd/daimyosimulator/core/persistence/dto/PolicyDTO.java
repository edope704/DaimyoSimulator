package it.unipd.daimyosimulator.core.persistence.dto;

import it.unipd.daimyosimulator.core.policy.PolicyType;

import java.util.EnumMap;
import java.util.Map;

public final class PolicyDTO {
    public PolicyType activePolicy;
    public int activeRemainingTicks;
    public Map<PolicyType, Integer> cooldowns = new EnumMap<>(PolicyType.class);
}
