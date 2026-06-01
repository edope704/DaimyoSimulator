package core;

import core.policy.MilitaryProtectionPolicy;
import core.villager.Role;
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
