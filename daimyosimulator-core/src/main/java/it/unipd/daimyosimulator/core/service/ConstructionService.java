package it.unipd.daimyosimulator.core.service;

import it.unipd.daimyosimulator.core.app.SnapshotMapper;
import it.unipd.daimyosimulator.core.app.result.PlacementResult;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.building.Building;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.factory.BuildingFactory;
import it.unipd.daimyosimulator.core.placement.CompositePlacementValidator;
import it.unipd.daimyosimulator.core.placement.PlacementCheck;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.core.villager.HousingStatus;
import it.unipd.daimyosimulator.core.villager.Role;

public final class ConstructionService {
    private final BuildingFactory buildingFactory;
    private final CompositePlacementValidator validator;
    private final HousingService housingService;
    private final VillageParameterCalculator parameterCalculator;
    private final SnapshotMapper snapshotMapper;

    public ConstructionService(
            BuildingFactory buildingFactory,
            CompositePlacementValidator validator,
            HousingService housingService,
            VillageParameterCalculator parameterCalculator,
            SnapshotMapper snapshotMapper
    ) {
        this.buildingFactory = buildingFactory;
        this.validator = validator;
        this.housingService = housingService;
        this.parameterCalculator = parameterCalculator;
        this.snapshotMapper = snapshotMapper;
    }

    public PlacementResult constructBuilding(Village village, BuildingType type, Position position) {
        VillageSnapshot before = snapshotMapper.toSnapshot(village);

        int remaining = village.getConfig().maxBuildsPerTick() - village.getBuildsThisTick();
        if (remaining <= 0) {
            return new PlacementResult(false,
                    "Build limit reached (" + village.getConfig().maxBuildsPerTick() + "/tick). Advance the tick first.",
                    before, before);
        }

        Building building = buildingFactory.create(type);
        PlacementCheck check = validator.validate(village, building, position);
        if (!check.success()) {
            return new PlacementResult(false, check.message(), before, before);
        }
        village.getResources().consume(ResourceType.TIMBER, building.getTimberCost());
        village.getGrid().placeBuilding(building, position);
        village.incrementBuildsThisTick();
        int newlyHoused = housingService.assignHousing(village);
        parameterCalculator.recalculate(village);
        String message = building.getDisplayName() + " placed at " + position;
        if (newlyHoused > 0) {
            message += "; housed " + newlyHoused + " villager(s)";
        }
        int buildsLeft = village.getConfig().maxBuildsPerTick() - village.getBuildsThisTick();
        if (buildsLeft > 0) {
            message += " [" + buildsLeft + " build(s) left this tick]";
        }
        village.addEvent(message);
        return new PlacementResult(true, message, before, snapshotMapper.toSnapshot(village));
    }

    /**
     * Removes the building at the given position. No timber refund is given.
     * Workers assigned to that building become idle; housing is re-evaluated.
     */
    public PlacementResult demolishBuilding(Village village, Position position) {
        VillageSnapshot before = snapshotMapper.toSnapshot(village);
        if (!village.getGrid().isInside(position)) {
            return new PlacementResult(false, "Position outside grid: " + position, before, before);
        }
        var cell = village.getGrid().getCell(position);
        if (cell.getBuilding().isEmpty()) {
            return new PlacementResult(false, "No building at " + position + " to demolish", before, before);
        }
        String name = cell.getBuilding().get().getDisplayName();
        // Unassign workers housed in this dwelling or working here.
        unassignWorkersForCell(village, cell.getBuilding().get().getType(), position);
        village.getGrid().removeBuilding(position);
        housingService.assignHousing(village);
        parameterCalculator.recalculate(village);
        String message = name + " at " + position + " demolished (no refund)";
        village.addEvent(message);
        return new PlacementResult(true, message, before, snapshotMapper.toSnapshot(village));
    }

    private void unassignWorkersForCell(Village village, BuildingType type, Position position) {
        var slots = buildingFactory.create(type).getJobSlots();
        for (var entry : slots.entrySet()) {
            Role role = entry.getKey();
            int slotsToUnassign = entry.getValue();
            int unassigned = 0;
            for (var v : village.getVillagers()) {
                if (unassigned >= slotsToUnassign) break;
                if (v.getRole() == role) {
                    v.assignRole(Role.IDLE);
                    unassigned++;
                }
            }
        }
        // If it was a dwelling, unhouse any residents recorded at this position.
        if (type == BuildingType.DWELLING) {
            int capacity = buildingFactory.create(type).getHousingCapacity();
            int unhoused = 0;
            for (var v : village.getVillagers()) {
                if (unhoused >= capacity) break;
                if (v.getHousingStatus() == HousingStatus.HOUSED
                        && position.equals(v.getDwellingPosition().orElse(null))) {
                    v.makeUnhoused();
                    unhoused++;
                }
            }
        }
    }
}
