package it.unipd.daimyosimulator.gdx.input;

import com.badlogic.gdx.Gdx;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.result.PlacementResult;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.assets.BuildingSpriteRegistry;
import it.unipd.daimyosimulator.gdx.assets.GameSoundManager;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

public final class InputCommandRouter {
    private final CoreGameFacade facade;
    private final BuildModeState buildModeState;
    private final Consumer<VillageSnapshot> snapshotConsumer;
    private final Consumer<String> messageConsumer;
    private final Consumer<CellViewModel> cellConsumer;
    private final GameSoundManager soundManager;
    private final Consumer<String> alertConsumer;
    private final Consumer<String> proximityWarningConsumer;
    private final BuildingSpriteRegistry buildingSpriteRegistry = new BuildingSpriteRegistry();
    /** Tracks which building types have already triggered a proximity warning this session. */
    private final Set<BuildingType> shownProximityWarnings = EnumSet.noneOf(BuildingType.class);

    public InputCommandRouter(
            CoreGameFacade facade,
            BuildModeState buildModeState,
            Consumer<VillageSnapshot> snapshotConsumer,
            Consumer<String> messageConsumer,
            Consumer<CellViewModel> cellConsumer,
            GameSoundManager soundManager,
            Consumer<String> alertConsumer,
            Consumer<String> proximityWarningConsumer
    ) {
        this.facade = facade;
        this.buildModeState = buildModeState;
        this.snapshotConsumer = snapshotConsumer;
        this.messageConsumer = messageConsumer;
        this.cellConsumer = cellConsumer;
        this.soundManager = soundManager;
        this.alertConsumer = alertConsumer;
        this.proximityWarningConsumer = proximityWarningConsumer;
    }

    /** Right-click or Escape: cancel current build/demolish mode. */
    public void handleCancel() {
        if (buildModeState.isActive()) {
            buildModeState.clear();
        }
    }

    public void handleGridClick(Position position) {
        if (buildModeState.isDemolishMode()) {
            PlacementResult result = facade.demolishBuilding(position);
            buildModeState.setLastPlacementValid(result.success());
            messageConsumer.accept(result.message());
            snapshotConsumer.accept(result.afterState());
            if (result.success()) {
                soundManager.playDemolish();
            }
            return;
        }

        if (buildModeState.isActive()) {
            buildModeState.selectedType().ifPresent(type -> {
                PlacementResult result = facade.constructBuilding(type, position);
                buildModeState.setPreviewPosition(position);
                buildModeState.setLastPlacementValid(result.success());
                messageConsumer.accept(result.message());
                if (Gdx.app != null) {
                    Gdx.app.log("DaimyoSimulator", "BUILD_SPRITE type=" + type
                            + " key=" + buildingSpriteRegistry.spriteName(type)
                            + " position=" + position);
                }
                snapshotConsumer.accept(result.afterState());
                if (result.success()) {
                    soundManager.playBuild();
                    buildModeState.clear();
                    checkProximityWarning(type, position, result.afterState());
                } else {
                    alertConsumer.accept(result.message());
                }
            });
            return;
        }

        // Normal inspect mode.
        try {
            CellViewModel cell = facade.inspectCell(position);
            cellConsumer.accept(cell);
        } catch (IllegalArgumentException e) {
            messageConsumer.accept(e.getMessage());
        }
        snapshotConsumer.accept(facade.getCurrentSnapshot());
    }

    private void checkProximityWarning(BuildingType type, Position pos, VillageSnapshot snapshot) {
        switch (type) {
            case RICE_PADDY -> {
                if (!hasNearby(snapshot, pos, BuildingType.RICE_FARM)
                        && shownProximityWarnings.add(BuildingType.RICE_PADDY)) {
                    proximityWarningConsumer.accept(
                            "Structure Inactive: Requires a Farm in close proximity to function.");
                }
            }
            case SMITHY -> {
                if (!hasNearby(snapshot, pos, BuildingType.MINE)
                        && shownProximityWarnings.add(BuildingType.SMITHY)) {
                    proximityWarningConsumer.accept(
                            "Structure Inactive: Requires a Mine in close proximity to function.");
                }
            }
            case WORKSHOP -> {
                if (!hasNearby(snapshot, pos, BuildingType.MINE)
                        && shownProximityWarnings.add(BuildingType.WORKSHOP)) {
                    proximityWarningConsumer.accept(
                            "Structure Inactive: Requires a Mine in close proximity to function.");
                }
            }
            default -> { }
        }
    }

    private static boolean hasNearby(VillageSnapshot snapshot, Position pos, BuildingType target) {
        return snapshot.cells().stream().anyMatch(c ->
                c.building() != null
                && c.building().type() == target
                && Math.abs(c.position().x() - pos.x()) <= 1
                && Math.abs(c.position().y() - pos.y()) <= 1);
    }
}
