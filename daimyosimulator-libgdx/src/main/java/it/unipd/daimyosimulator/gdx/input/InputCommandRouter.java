package it.unipd.daimyosimulator.gdx.input;

import com.badlogic.gdx.Gdx;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.result.PlacementResult;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.assets.BuildingSpriteRegistry;
import it.unipd.daimyosimulator.gdx.assets.GameSoundManager;

import java.util.function.Consumer;

public final class InputCommandRouter {
    private final CoreGameFacade facade;
    private final BuildModeState buildModeState;
    private final Consumer<VillageSnapshot> snapshotConsumer;
    private final Consumer<String> messageConsumer;
    private final Consumer<CellViewModel> cellConsumer;
    private final GameSoundManager soundManager;
    private final BuildingSpriteRegistry buildingSpriteRegistry = new BuildingSpriteRegistry();

    public InputCommandRouter(
            CoreGameFacade facade,
            BuildModeState buildModeState,
            Consumer<VillageSnapshot> snapshotConsumer,
            Consumer<String> messageConsumer,
            Consumer<CellViewModel> cellConsumer,
            GameSoundManager soundManager
    ) {
        this.facade = facade;
        this.buildModeState = buildModeState;
        this.snapshotConsumer = snapshotConsumer;
        this.messageConsumer = messageConsumer;
        this.cellConsumer = cellConsumer;
        this.soundManager = soundManager;
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
            // Stay in demolish mode so player can remove multiple buildings.
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
}
