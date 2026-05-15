package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.factory.PolicyFactory;
import it.unipd.daimyosimulator.core.policy.PolicyManager;
import it.unipd.daimyosimulator.core.policy.PolicyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PolicyManagerTest {
    @Test
    void onlyOnePolicyActiveAndCooldownStartsOnExpiry() {
        PolicyManager manager = new PolicyManager(new PolicyFactory());
        assertTrue(manager.activate(PolicyType.AGRICULTURAL_EXPANSION, TestFixtures.config()).success());
        assertFalse(manager.activate(PolicyType.MILITARY_PROTECTION, TestFixtures.config()).success());
        for (int i = 0; i < TestFixtures.config().policyDurationTicks(); i++) {
            manager.advanceTick(TestFixtures.config());
        }
        assertFalse(manager.isPolicyActive());
        assertTrue(manager.getCooldown(PolicyType.AGRICULTURAL_EXPANSION) > 0);
    }
}
