package it.unipd.daimyosimulator.core.app;

import it.unipd.daimyosimulator.core.app.view.*;
import it.unipd.daimyosimulator.core.building.Building;
import it.unipd.daimyosimulator.core.domain.Cell;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.domain.VillageParameters;
import it.unipd.daimyosimulator.core.policy.PolicyManager;
import it.unipd.daimyosimulator.core.resource.ResourceStock;
import it.unipd.daimyosimulator.core.villager.HousingStatus;
import it.unipd.daimyosimulator.core.villager.Role;
import it.unipd.daimyosimulator.core.villager.Villager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class SnapshotMapper {
    public VillageSnapshot toSnapshot(Village village) {
        List<CellViewModel> cells = new ArrayList<>();
        for (Cell cell : village.getGrid().getCells()) {
            cells.add(toCellViewModel(cell));
        }
        return new VillageSnapshot(
                village.getGrid().getWidth(),
                village.getGrid().getHeight(),
                village.getTickNumber(),
                cells,
                toResourceViewModel(village.getResources()),
                toPopulationViewModel(village.getVillagers()),
                toParametersViewModel(village.getParameters()),
                toPolicyViewModel(village.getPolicyManager()),
                latest(village.getEventHistory(), 10),
                village.getBuildsThisTick(),
                village.getConfig().maxBuildsPerTick(),
                village.getMarketCooldownTicks()
        );
    }

    public DashboardViewModel toDashboard(Village village) {
        VillageSnapshot snapshot = toSnapshot(village);
        return new DashboardViewModel(
                snapshot.resources(),
                snapshot.population(),
                snapshot.parameters(),
                snapshot.policy(),
                snapshot.tick(),
                new EventLogViewModel(snapshot.latestEvents()),
                null
        );
    }

    public CellViewModel toCellViewModel(Cell cell) {
        return new CellViewModel(
                cell.getPosition(),
                cell.getNaturalFeature().orElse(null),
                cell.getBuilding().map(this::toBuildingViewModel).orElse(null)
        );
    }

    public BuildingViewModel toBuildingViewModel(Building building) {
        return new BuildingViewModel(
                building.getType(),
                building.getDisplayName(),
                building.getTimberCost(),
                building.getHousingCapacity(),
                building.getJobSlots()
        );
    }

    public ResourceViewModel toResourceViewModel(ResourceStock stock) {
        return new ResourceViewModel(stock.getRice(), stock.getTimber(), stock.getTools(), stock.getLuxuryGoods());
    }

    public VillageParametersViewModel toParametersViewModel(VillageParameters parameters) {
        return new VillageParametersViewModel(
                parameters.getHappiness(),
                parameters.getProtection(),
                parameters.getFood(),
                parameters.getFaith(),
                parameters.getHousing(),
                parameters.getCraftsmanship()
        );
    }

    public PolicyViewModel toPolicyViewModel(PolicyManager manager) {
        return new PolicyViewModel(
                manager.getActiveType().orElse(null),
                manager.getActivePolicy().getDisplayName(),
                manager.getActiveRemainingTicks(),
                manager.getCooldowns()
        );
    }

    public PopulationViewModel toPopulationViewModel(List<Villager> villagers) {
        EnumMap<Role, Integer> counts = new EnumMap<>(Role.class);
        for (Role role : Role.values()) {
            counts.put(role, 0);
        }
        int idle = 0;
        int unhoused = 0;
        int employed = 0;
        for (Villager villager : villagers) {
            counts.merge(villager.getRole(), 1, Integer::sum);
            if (villager.getRole() == Role.IDLE) {
                idle++;
            }
            if (villager.getHousingStatus() == HousingStatus.UNHOUSED) {
                unhoused++;
            }
            if (villager.isEmployed()) {
                employed++;
            }
        }
        return new PopulationViewModel(villagers.size(), idle, unhoused, employed, Map.copyOf(counts));
    }

    private static List<String> latest(List<String> events, int limit) {
        int from = Math.max(0, events.size() - limit);
        return events.subList(from, events.size());
    }
}
