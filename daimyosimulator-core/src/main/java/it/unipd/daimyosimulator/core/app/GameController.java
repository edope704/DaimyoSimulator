package it.unipd.daimyosimulator.core.app;

import it.unipd.daimyosimulator.core.app.result.*;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.app.view.DashboardViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.config.GameConfig;
import it.unipd.daimyosimulator.core.domain.Grid;
import it.unipd.daimyosimulator.core.domain.NaturalFeature;
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

    /**
     * Places 1 WoodcuttersHut (adjacent to a forest) and 2 adjacent Dwellings directly
     * on the current village grid, bypassing cost and build-limit validation.
     * Called only from the UI layer when the player starts a brand-new game.
     */
    public VillageSnapshot placeStarterBuildings() {
        Grid grid = currentVillage.getGrid();
        int width = currentVillage.getConfig().gridWidth();
        int height = currentVillage.getConfig().gridHeight();
        BuildingFactory factory = new BuildingFactory(baseConfig);

        // WoodcuttersHut: first empty cell adjacent to a forest.
        Position woodPos = null;
        scan1:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Position p = new Position(x, y);
                if (grid.getCell(p).isEmpty()
                        && grid.hasNaturalFeatureWithin(p, NaturalFeature.FOREST, 1)) {
                    woodPos = p;
                    break scan1;
                }
            }
        }
        if (woodPos != null) {
            grid.placeBuilding(factory.create(BuildingType.WOODCUTTERS_HUT), woodPos);
        }

        // Two adjacent Dwellings: search outward from the map centre in expanding
        // square zones.  isEmpty() returns false for both buildings and natural
        // features, so forest cells are automatically skipped.
        int cx = width / 2;
        int cy = height / 2;
        int[] dx4 = {1, 0, -1, 0};
        int[] dy4 = {0, 1, 0, -1};
        Position d1 = null, d2 = null;
        int maxZone = Math.max(width, height);
        for (int zone = 2; zone <= maxZone && d1 == null; zone++) {
            int x0 = Math.max(0, cx - zone), x1 = Math.min(width - 1, cx + zone);
            int y0 = Math.max(0, cy - zone), y1 = Math.min(height - 1, cy + zone);
            sweep:
            for (int x = x0; x <= x1; x++) {
                for (int y = y0; y <= y1; y++) {
                    Position p1 = new Position(x, y);
                    if (!grid.getCell(p1).isEmpty()) continue;
                    for (int dd = 0; dd < 4; dd++) {
                        int nx = x + dx4[dd], ny = y + dy4[dd];
                        if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                        Position p2 = new Position(nx, ny);
                        if (grid.getCell(p2).isEmpty()) { d1 = p1; d2 = p2; break sweep; }
                    }
                }
            }
        }
        if (d1 != null) grid.placeBuilding(factory.create(BuildingType.DWELLING), d1);
        if (d2 != null) grid.placeBuilding(factory.create(BuildingType.DWELLING), d2);

        housingService.assignHousing(currentVillage);
        parameterCalculator.recalculate(currentVillage);
        return getCurrentSnapshot();
    }

    Village currentVillageForTests() {
        return currentVillage;
    }
}
