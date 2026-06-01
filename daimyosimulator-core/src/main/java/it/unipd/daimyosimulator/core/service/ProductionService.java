package it.unipd.daimyosimulator.core.service;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Cell;
import it.unipd.daimyosimulator.core.domain.NaturalFeature;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.policy.PolicyStrategy;
import it.unipd.daimyosimulator.core.resource.ResourceStock;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.core.villager.Role;

public final class ProductionService {
    public ResourceStock produce(Village village) {
        ResourceStock produced = new ResourceStock();
        PolicyStrategy policy = village.getPolicyManager().getActivePolicy();
        int riceFarmers = (int) village.countRole(Role.RICE_FARMER);
        int woodcutters = (int) village.countRole(Role.WOODCUTTER);
        int blacksmiths = (int) village.countRole(Role.BLACKSMITH);
        int artisans = (int) village.countRole(Role.ARTISAN);

        int eligiblePaddies = 0;
        for (Cell cell : village.getGrid().getCells()) {
            if (cell.getBuilding().filter(building -> building.getType() == BuildingType.RICE_PADDY).isPresent()
                    && village.getGrid().hasBuildingWithin(cell.getPosition(), BuildingType.RICE_FARM, village.getConfig().adjacencyRange())) {
                eligiblePaddies++;
            }
        }
        if (riceFarmers > 0 && eligiblePaddies > 0) {
            addProduced(village, produced, ResourceType.RICE, BuildingType.RICE_PADDY, eligiblePaddies * 5, policy);
        }

        long validHuts = village.getGrid().getCells().stream()
                .filter(cell -> cell.getBuilding().filter(building -> building.getType() == BuildingType.WOODCUTTERS_HUT).isPresent())
                .filter(cell -> village.getGrid().hasNaturalFeatureWithin(cell.getPosition(), NaturalFeature.FOREST,
                        village.getConfig().adjacencyRange()))
                .count();
        if (validHuts > 0 && woodcutters > 0) {
            addProduced(village, produced, ResourceType.TIMBER, BuildingType.WOODCUTTERS_HUT, woodcutters * 1, policy);
        }

        if (village.getGrid().hasBuilding(BuildingType.MINE) && blacksmiths > 0) {
            addProduced(village, produced, ResourceType.TOOLS, BuildingType.SMITHY, blacksmiths * 2, policy);
        }

        if (village.getGrid().hasBuilding(BuildingType.MINE)
                && artisans > 0
                && village.getTickNumber() % village.getConfig().workshopProductionIntervalTicks() == 0) {
            addProduced(village, produced, ResourceType.LUXURY_GOODS, BuildingType.WORKSHOP, artisans * 2, policy);
        }
        return produced;
    }

    private void addProduced(Village village, ResourceStock produced, ResourceType resourceType,
                             BuildingType buildingType, int baseAmount, PolicyStrategy policy) {
        int amount = (int) Math.ceil(baseAmount * policy.productionMultiplier(resourceType, buildingType));
        if (amount > 0) {
            village.getResources().add(resourceType, amount);
            produced.add(resourceType, amount);
        }
    }
}
