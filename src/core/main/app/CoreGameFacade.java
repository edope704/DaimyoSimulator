package core.app;

import core.app.result.*;
import core.app.view.CellViewModel;
import core.app.view.DashboardViewModel;
import core.app.view.VillageSnapshot;
import core.building.BuildingType;
import core.config.GameConfig;
import core.domain.Position;
import core.policy.PolicyType;
import core.random.JavaRandomProvider;
import core.random.RandomProvider;

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

    /** Places free starter buildings (1 WoodcuttersHut + 2 Dwellings) on a brand-new village. */
    public VillageSnapshot applyStarterBuildings() {
        return controller.placeStarterBuildings();
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

    public SaveResult saveVillage(int slot) {
        return controller.saveVillage(slotPath(slot));
    }

    public LoadResult loadVillage(Path path) {
        return controller.loadVillage(path);
    }

    public LoadResult loadVillage(int slot) {
        return controller.loadVillage(slotPath(slot));
    }

    public TradeResult requestTrade(TradeRequest request) {
        return controller.requestTrade(request);
    }

    // ── Save-slot helpers ─────────────────────────────────────────────────────

    public static Path defaultSavePath() {
        return slotPath(1);
    }

    public static Path saveDir() {
        return Path.of(System.getProperty("user.home"), ".daimyosimulator");
    }

    public static Path slotPath(int slot) {
        return saveDir().resolve("savegame_" + slot + ".json");
    }

    /** Returns info about all five save slots (exists flag + file label). */
    public static java.util.List<SaveSlotInfo> listSaveSlots() {
        java.util.List<SaveSlotInfo> slots = new java.util.ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Path p = slotPath(i);
            boolean exists = java.nio.file.Files.exists(p);
            String label = exists ? "Slot " + i + "  [saved]" : "Slot " + i + "  [empty]";
            slots.add(new SaveSlotInfo(i, exists, label));
        }
        return slots;
    }

    public record SaveSlotInfo(int slot, boolean exists, String label) {}
}
