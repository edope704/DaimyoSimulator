package it.unipd.daimyosimulator.core.app;

import it.unipd.daimyosimulator.core.app.result.*;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.app.view.DashboardViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.config.GameConfig;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.domain.Village;
import it.unipd.daimyosimulator.core.event.RandomEventManager;
import it.unipd.daimyosimulator.core.factory.BuildingFactory;
import it.unipd.daimyosimulator.core.placement.CompositePlacementValidator;
import it.unipd.daimyosimulator.core.policy.PolicyActivation;
import it.unipd.daimyosimulator.core.policy.PolicyType;
import it.unipd.daimyosimulator.core.persistence.VillageMapper;
import it.unipd.daimyosimulator.core.persistence.VillagePersistenceService;
import it.unipd.daimyosimulator.core.random.JavaRandomProvider;
import it.unipd.daimyosimulator.core.random.RandomProvider;
import it.unipd.daimyosimulator.core.service.*;
import it.unipd.daimyosimulator.core.simulation.SimulationEngine;
import it.unipd.daimyosimulator.core.simulation.TickProcessor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public final class GameController {
    private final GameConfig baseConfig;
    private final RandomProvider randomProvider;
    private final SnapshotMapper snapshotMapper = new SnapshotMapper();
    private final HousingService housingService = new HousingService();
    private final HappinessCalculator happinessCalculator = new HappinessCalculator();
    private final VillageParameterCalculator parameterCalculator =
            new VillageParameterCalculator(housingService, happinessCalculator);
    private final ConstructionService constructionService;
    private final SimulationEngine simulationEngine;
    private final TradeService tradeService;
    private final VillagePersistenceService persistenceService;
    private Village currentVillage;

    public GameController() {
        this(GameConfig.defaults(), new JavaRandomProvider(1200L));
    }

    public GameController(GameConfig config, RandomProvider randomProvider) {
        this.baseConfig = Objects.requireNonNull(config, "config");
        this.randomProvider = Objects.requireNonNull(randomProvider, "randomProvider");
        this.constructionService = new ConstructionService(
                new BuildingFactory(baseConfig),
                new CompositePlacementValidator(),
                housingService,
                parameterCalculator,
                snapshotMapper
        );
        this.simulationEngine = new SimulationEngine(new TickProcessor(
                snapshotMapper,
                new JobAssignmentService(randomProvider),
                new ProductionService(),
                new ConsumptionService(),
                new ShortageService(),
                parameterCalculator,
                new BirthDeathService(housingService, randomProvider),
                new RandomEventManager(randomProvider)
        ));
        this.tradeService = new TradeService(snapshotMapper);
        this.persistenceService = new VillagePersistenceService(new VillageMapper());
        startNewVillage(baseConfig.gridWidth(), baseConfig.gridHeight());
    }

    public VillageSnapshot startNewVillage(int width, int height) {
        currentVillage = new VillageInitializer(baseConfig.withGridSize(width, height), randomProvider)
                .createVillage(width, height);
        return snapshotMapper.toSnapshot(currentVillage);
    }

    public VillageSnapshot getCurrentSnapshot() {
        return snapshotMapper.toSnapshot(currentVillage);
    }

    public DashboardViewModel getDashboard() {
        return snapshotMapper.toDashboard(currentVillage);
    }

    public PlacementResult constructBuilding(BuildingType type, Position position) {
        return constructionService.constructBuilding(currentVillage, type, position);
    }

    public PlacementResult demolishBuilding(Position position) {
        return constructionService.demolishBuilding(currentVillage, position);
    }

    public TickResult advanceTick() {
        return simulationEngine.advanceTick(currentVillage);
    }

    public PolicyActivationResult activatePolicy(PolicyType type) {
        PolicyActivation activation = currentVillage.getPolicyManager().activate(type, currentVillage.getConfig());
        currentVillage.addEvent(activation.message());
        return new PolicyActivationResult(
                activation.success(),
                activation.message(),
                currentVillage.getPolicyManager().getActiveType().orElse(null),
                getDashboard()
        );
    }

    public CellViewModel inspectCell(Position position) {
        if (!currentVillage.getGrid().isInside(position)) {
            throw new IllegalArgumentException("Position outside grid: " + position);
        }
        return snapshotMapper.toCellViewModel(currentVillage.getGrid().getCell(position));
    }

    public SaveResult saveVillage(Path path) {
        try {
            persistenceService.save(currentVillage, path);
            return new SaveResult(true, "Village saved to " + path, path);
        } catch (IOException | RuntimeException e) {
            return new SaveResult(false, "Could not save village: " + e.getMessage(), path);
        }
    }

    public LoadResult loadVillage(Path path) {
        try {
            currentVillage = persistenceService.load(path, baseConfig);
            return new LoadResult(true, "Village loaded from " + path, path, getCurrentSnapshot());
        } catch (IOException | RuntimeException e) {
            return new LoadResult(false, "Could not load village: " + e.getMessage(), path, getCurrentSnapshot());
        }
    }

    public TradeResult requestTrade(TradeRequest request) {
        return tradeService.trade(currentVillage, request);
    }

    Village currentVillageForTests() {
        return currentVillage;
    }
}
