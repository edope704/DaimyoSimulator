package it.unipd.daimyosimulator.core.service;

import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Cell;
import it.unipd.daimyosimulator.core.domain.NaturalFeature;
import it.unipd.daimyosimulator.core.domain.Position;
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
        int artisans    = (int) village.countRole(Role.ARTISAN);
        int range       = village.getConfig().adjacencyRange();

        // Rice: paddies adjacent to farms
        int eligiblePaddies = 0;
        for (Cell cell : village.getGrid().getCells()) {
            if (cell.getBuilding().filter(b -> b.getType() == BuildingType.RICE_PADDY).isPresent()
                    && village.getGrid().hasBuildingWithin(cell.getPosition(), BuildingType.RICE_FARM, range)) {
                eligiblePaddies++;
            }
        }
        if (riceFarmers > 0 && eligiblePaddies > 0) {
            // Tools shortage: farmers work at half efficiency when the tool stock is empty.
            int ricePerPaddy = village.getResources().getTools() == 0 ? 2 : 5;
            addProduced(village, produced, ResourceType.RICE, BuildingType.RICE_PADDY, eligiblePaddies * ricePerPaddy, policy);
        }

        // Timber: woodcutter huts adjacent to a playable forest tile OR the outer tree border
        long validHuts = village.getGrid().getCells().stream()
                .filter(c -> c.getBuilding().filter(b -> b.getType() == BuildingType.WOODCUTTERS_HUT).isPresent())
                .filter(c -> isNearForestOrBorder(village, c.getPosition(), range))
                .count();
        if (validHuts > 0 && woodcutters > 0) {
            addProduced(village, produced, ResourceType.TIMBER, BuildingType.WOODCUTTERS_HUT, woodcutters, policy);
        }

        // Tools: smithies adjacent to a mine (proximity-based)
        long activeSmithies = village.getGrid().getCells().stream()
                .filter(c -> c.getBuilding().filter(b -> b.getType() == BuildingType.SMITHY).isPresent())
                .filter(c -> village.getGrid().hasBuildingWithin(c.getPosition(), BuildingType.MINE, range))
                .count();
        if (activeSmithies > 0 && blacksmiths > 0) {
            addProduced(village, produced, ResourceType.TOOLS, BuildingType.SMITHY, blacksmiths * 2, policy);
        }

        // Luxury: workshops adjacent to a mine (proximity-based)
        long activeWorkshops = village.getGrid().getCells().stream()
                .filter(c -> c.getBuilding().filter(b -> b.getType() == BuildingType.WORKSHOP).isPresent())
                .filter(c -> village.getGrid().hasBuildingWithin(c.getPosition(), BuildingType.MINE, range))
                .count();
        if (activeWorkshops > 0 && artisans > 0
                && village.getTickNumber() % village.getConfig().workshopProductionIntervalTicks() == 0) {
            addProduced(village, produced, ResourceType.LUXURY_GOODS, BuildingType.WORKSHOP, artisans * 2, policy);
        }
        return produced;
    }

    /** A woodcutter hut is operational if it is adjacent to a playable forest tile
     *  OR within {@code range} cells of any grid edge (the outer decorative tree border). */
    private boolean isNearForestOrBorder(Village village, Position pos, int range) {
        if (village.getGrid().hasNaturalFeatureWithin(pos, NaturalFeature.FOREST, range)) return true;
        int w = village.getConfig().gridWidth();
        int h = village.getConfig().gridHeight();
        return pos.x() < range || pos.y() < range
                || pos.x() >= w - range || pos.y() >= h - range;
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
