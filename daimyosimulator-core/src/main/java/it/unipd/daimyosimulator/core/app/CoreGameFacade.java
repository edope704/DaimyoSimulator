package it.unipd.daimyosimulator.core.app;

import it.unipd.daimyosimulator.core.app.result.*;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.app.view.DashboardViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.config.GameConfig;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.core.policy.PolicyType;
import it.unipd.daimyosimulator.core.random.JavaRandomProvider;
import it.unipd.daimyosimulator.core.random.RandomProvider;

import java.nio.file.Path;

public final class CoreGameFacade {
    private final GameController controller;

    public CoreGameFacade() {
        this(new GameController());
    }

    public CoreGameFacade(GameConfig config, RandomProvider randomProvider) {
        this(new GameController(config, randomProvider));
    }

    public CoreGameFacade(GameConfig config, long seed) {
        this(config, new JavaRandomProvider(seed));
    }

    public CoreGameFacade(GameController controller) {
        this.controller = controller;
    }

    public VillageSnapshot startNewVillage(int width, int height) {
        return controller.startNewVillage(width, height);
    }

    public VillageSnapshot getCurrentSnapshot() {
        return controller.getCurrentSnapshot();
    }

    public DashboardViewModel getDashboard() {
        return controller.getDashboard();
    }

    public PlacementResult constructBuilding(BuildingType type, Position position) {
        return controller.constructBuilding(type, position);
    }

    public PlacementResult demolishBuilding(Position position) {
        return controller.demolishBuilding(position);
    }

    public TickResult advanceTick() {
        return controller.advanceTick();
    }

    public PolicyActivationResult activatePolicy(PolicyType type) {
        return controller.activatePolicy(type);
    }

    public CellViewModel inspectCell(Position position) {
        return controller.inspectCell(position);
    }

    public SaveResult saveVillage(Path path) {
        return controller.saveVillage(path);
    }

    public LoadResult loadVillage(Path path) {
        return controller.loadVillage(path);
    }

    public TradeResult requestTrade(TradeRequest request) {
        return controller.requestTrade(request);
    }

    public static Path defaultSavePath() {
        return Path.of(System.getProperty("user.home"), ".daimyosimulator", "savegame.json");
    }
}
