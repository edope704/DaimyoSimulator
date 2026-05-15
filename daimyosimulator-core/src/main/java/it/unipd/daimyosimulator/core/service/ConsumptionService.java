package it.unipd.daimyosimulator.core.service;

import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.policy.PolicyStrategy;
import it.unipd.daimyosimulator.core.resource.ResourceStock;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.core.villager.Role;
import it.unipd.daimyosimulator.core.villager.Villager;

import java.util.ArrayList;
import java.util.List;

public final class ConsumptionService {
    public ConsumptionResult consume(Village village) {
        ResourceStock requested = new ResourceStock();
        PolicyStrategy policy = village.getPolicyManager().getActivePolicy();
        for (Villager villager : village.getVillagers()) {
            Role role = villager.getRole();
            requested.add(ResourceType.RICE, (int) Math.ceil(village.getConfig().ricePerVillagerPerTick()
                    * policy.riceConsumptionMultiplier(role)));
            if (role == Role.RICE_FARMER || role == Role.SAMURAI) {
                requested.add(ResourceType.TOOLS, (int) Math.ceil(policy.toolsConsumptionMultiplier(role)));
            }
            if (role == Role.SAMURAI || role == Role.MONK) {
                requested.add(ResourceType.LUXURY_GOODS, (int) Math.ceil(policy.luxuryConsumptionMultiplier(role)));
            }
        }

        ResourceStock consumed = new ResourceStock();
        List<String> shortages = new ArrayList<>();
        for (ResourceType type : ResourceType.values()) {
            int asked = requested.get(type);
            int actual = village.getResources().consumeUpTo(type, asked);
            consumed.add(type, actual);
            if (actual < asked) {
                shortages.add("Shortage: needed " + asked + " " + type + " but consumed " + actual);
            }
        }
        return new ConsumptionResult(requested, consumed, shortages);
    }
}
