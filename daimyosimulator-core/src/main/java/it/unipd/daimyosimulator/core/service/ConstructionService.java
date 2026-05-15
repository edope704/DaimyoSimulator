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
        Building building = buildingFactory.create(type);
        PlacementCheck check = validator.validate(village, building, position);
        if (!check.success()) {
            return new PlacementResult(false, check.message(), before, before);
        }
        village.getResources().consume(ResourceType.TIMBER, building.getTimberCost());
        village.getGrid().placeBuilding(building, position);
        int newlyHoused = housingService.assignHousing(village);
        parameterCalculator.recalculate(village);
        String message = building.getDisplayName() + " placed at " + position;
        if (newlyHoused > 0) {
            message += "; housed " + newlyHoused + " villager(s)";
        }
        village.addEvent(message);
        return new PlacementResult(true, message, before, snapshotMapper.toSnapshot(village));
    }
}
