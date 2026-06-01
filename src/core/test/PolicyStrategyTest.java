package core;

import core.factory.PolicyFactory;
import core.policy.PolicyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolicyStrategyTest {
    @Test
    void factoryReturnsRequestedStrategy() {
        assertEquals(PolicyType.MILITARY_PROTECTION, new PolicyFactory().create(PolicyType.MILITARY_PROTECTION).getType());
    }
}
