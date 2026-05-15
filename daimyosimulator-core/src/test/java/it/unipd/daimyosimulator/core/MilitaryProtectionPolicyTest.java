package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.policy.MilitaryProtectionPolicy;
import it.unipd.daimyosimulator.core.villager.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MilitaryProtectionPolicyTest {
    @Test
    void militaryPolicyBoostsProtectionAndSamuraiCosts() {
        var policy = new MilitaryProtectionPolicy();
        assertEquals(1.5, policy.protectionMultiplier());
        assertEquals(1.5, policy.luxuryConsumptionMultiplier(Role.SAMURAI));
    }
}
