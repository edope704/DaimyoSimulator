package it.unipd.daimyosimulator.core.persistence;

import it.unipd.daimyosimulator.core.building.Building;
import it.unipd.daimyosimulator.core.config.GameConfig;
import it.unipd.daimyosimulator.core.domain.*;
import it.unipd.daimyosimulator.core.factory.BuildingFactory;
import it.unipd.daimyosimulator.core.factory.PolicyFactory;
import it.unipd.daimyosimulator.core.persistence.dto.*;
import it.unipd.daimyosimulator.core.policy.PolicyManager;
import it.unipd.daimyosimulator.core.policy.PolicyType;
import it.unipd.daimyosimulator.core.resource.ResourceStock;
import it.unipd.daimyosimulator.core.villager.HousingStatus;
import it.unipd.daimyosimulator.core.villager.Role;
import it.unipd.daimyosimulator.core.villager.Villager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class VillageMapper {
    public VillageDTO toDTO(Village village) {
        VillageDTO dto = new VillageDTO();
        dto.tickNumber = village.getTickNumber();
        dto.grid = toGridDTO(village);
        dto.resources = toResourceDTO(village.getResources());
        dto.parameters = toParametersDTO(village.getParameters());
        dto.villagers = village.getVillagers().stream().map(this::toVillagerDTO).toList();
        dto.policy = toPolicyDTO(village.getPolicyManager());
        dto.birthProgress = village.getBirthProgress();
        dto.starvationTicks = village.getStarvationTicks();
        dto.randomEventsEnabled = village.getConfig().randomEventsEnabled();
        dto.eventHistory = village.getEventHistory();
        dto.marketCooldownTicks = village.getMarketCooldownTicks();
        return dto;
    }

    public Village fromDTO(VillageDTO dto, GameConfig config) {
        if (dto == null || dto.version != 1) {
            throw new IllegalArgumentException("Unsupported or missing save version");
        }
        if (dto.grid == null || dto.resources == null || dto.parameters == null || dto.policy == null) {
            throw new IllegalArgumentException("Save file is missing required sections");
        }
        GameConfig restoredConfig = config.withGridSize(dto.grid.width, dto.grid.height)
                .withRandomEventsEnabled(dto.randomEventsEnabled);
        Grid grid = new Grid(dto.grid.width, dto.grid.height);
        BuildingFactory buildingFactory = new BuildingFactory(restoredConfig);
        for (CellDTO cellDTO : dto.grid.cells) {
            Position position = new Position(cellDTO.x, cellDTO.y);
            if (!grid.isInside(position)) {
                throw new IllegalArgumentException("Save file contains cell outside grid: " + position);
            }
            if (cellDTO.naturalFeature != null) {
                grid.placeNaturalFeature(cellDTO.naturalFeature, position);
            } else if (cellDTO.building != null && cellDTO.building.type != null) {
                Building building = buildingFactory.create(cellDTO.building.type);
                grid.placeBuilding(building, position);
            }
        }

        List<Villager> villagers = new ArrayList<>();
        for (VillagerDTO villagerDTO : dto.villagers) {
            HousingStatus housingStatus = villagerDTO.housingStatus == null ? HousingStatus.UNHOUSED : villagerDTO.housingStatus;
            Villager villager = new Villager(villagerDTO.id, housingStatus);
            if (housingStatus == HousingStatus.HOUSED && villagerDTO.dwellingPosition != null) {
                villager.houseAt(new Position(villagerDTO.dwellingPosition.x, villagerDTO.dwellingPosition.y));
            }
            villager.assignRole(villagerDTO.role == null ? Role.UNHOUSED : villagerDTO.role);
            villagers.add(villager);
        }

        PolicyManager policyManager = new PolicyManager(new PolicyFactory());
        EnumMap<PolicyType, Integer> cooldowns = new EnumMap<>(PolicyType.class);
        if (dto.policy.cooldowns != null) {
            cooldowns.putAll(dto.policy.cooldowns);
        }
        policyManager.restore(dto.policy.activePolicy, dto.policy.activeRemainingTicks, cooldowns);

        Village village = new Village(
                grid,
                new ResourceStock(dto.resources.rice, dto.resources.timber, dto.resources.tools, dto.resources.luxuryGoods),
                new VillageParameters(dto.parameters.happiness, dto.parameters.protection, dto.parameters.food,
                        dto.parameters.faith, dto.parameters.housing, dto.parameters.craftsmanship),
                villagers,
                policyManager,
                restoredConfig
        );
        village.setTickNumber(dto.tickNumber);
        village.setBirthProgress(dto.birthProgress);
        village.setStarvationTicks(dto.starvationTicks);
        village.setMarketCooldownTicks(dto.marketCooldownTicks);
        village.restoreEventHistory(dto.eventHistory);
        return village;
    }

    private GridDTO toGridDTO(Village village) {
        GridDTO dto = new GridDTO();
        dto.width = village.getGrid().getWidth();
        dto.height = village.getGrid().getHeight();
        for (Cell cell : village.getGrid().getCells()) {
            CellDTO cellDTO = new CellDTO();
            cellDTO.x = cell.getPosition().x();
            cellDTO.y = cell.getPosition().y();
            cellDTO.naturalFeature = cell.getNaturalFeature().orElse(null);
            cellDTO.building = cell.getBuilding().map(this::toBuildingDTO).orElse(null);
            dto.cells.add(cellDTO);
        }
        return dto;
    }

    private BuildingDTO toBuildingDTO(Building building) {
        BuildingDTO dto = new BuildingDTO();
        dto.type = building.getType();
        return dto;
    }

    private ResourceStockDTO toResourceDTO(ResourceStock stock) {
        ResourceStockDTO dto = new ResourceStockDTO();
        dto.rice = stock.getRice();
        dto.timber = stock.getTimber();
        dto.tools = stock.getTools();
        dto.luxuryGoods = stock.getLuxuryGoods();
        return dto;
    }

    private VillageParametersDTO toParametersDTO(VillageParameters parameters) {
        VillageParametersDTO dto = new VillageParametersDTO();
        dto.happiness = parameters.getHappiness();
        dto.protection = parameters.getProtection();
        dto.food = parameters.getFood();
        dto.faith = parameters.getFaith();
        dto.housing = parameters.getHousing();
        dto.craftsmanship = parameters.getCraftsmanship();
        return dto;
    }

    private VillagerDTO toVillagerDTO(Villager villager) {
        VillagerDTO dto = new VillagerDTO();
        dto.id = villager.getId();
        dto.role = villager.getRole();
        dto.housingStatus = villager.getHousingStatus();
        dto.dwellingPosition = villager.getDwellingPosition().map(position -> {
            PositionDTO positionDTO = new PositionDTO();
            positionDTO.x = position.x();
            positionDTO.y = position.y();
            return positionDTO;
        }).orElse(null);
        return dto;
    }

    private PolicyDTO toPolicyDTO(PolicyManager policyManager) {
        PolicyDTO dto = new PolicyDTO();
        dto.activePolicy = policyManager.getActiveType().orElse(null);
        dto.activeRemainingTicks = policyManager.getActiveRemainingTicks();
        dto.cooldowns = new EnumMap<>(policyManager.getCooldowns());
        return dto;
    }
}
