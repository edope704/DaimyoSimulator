package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.policy.CraftsmenProductionPolicy;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.core.villager.Role;
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
