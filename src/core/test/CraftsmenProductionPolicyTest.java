package core;

import core.building.BuildingType;
import core.policy.CraftsmenProductionPolicy;
import core.resource.ResourceType;
import core.villager.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CraftsmenProductionPolicyTest {
    @Test
    void craftsmenPolicyBoostsManufacturingAndCraftsmanRiceCost() {
        var policy = new CraftsmenProductionPolicy();
        assertEquals(1.5, policy.productionMultiplier(ResourceType.TOOLS, BuildingType.SMITHY));
        assertEquals(1.5, policy.riceConsumptionMultiplier(Role.ARTISAN));
    }
}
