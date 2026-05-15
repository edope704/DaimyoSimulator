package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.policy.AgriculturalExpansionPolicy;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.core.villager.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AgriculturalExpansionPolicyTest {
    @Test
    void agriculturalPolicyBoostsRiceAndFarmerTools() {
        var policy = new AgriculturalExpansionPolicy();
        assertEquals(1.5, policy.productionMultiplier(ResourceType.RICE, BuildingType.RICE_PADDY));
        assertEquals(1.5, policy.toolsConsumptionMultiplier(Role.RICE_FARMER));
    }
}
