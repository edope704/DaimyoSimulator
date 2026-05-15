package it.unipd.daimyosimulator.gdx.input;

import com.badlogic.gdx.Gdx;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.result.PlacementResult;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.assets.BuildingSpriteRegistry;

import java.util.function.Consumer;

public final class InputCommandRouter {
    private final CoreGameFacade facade;
    private final BuildModeState buildModeState;
    private final Consumer<VillageSnapshot> snapshotConsumer;
    private final Consumer<String> messageConsumer;
    private final Consumer<CellViewModel> cellConsumer;
    private final BuildingSpriteRegistry buildingSpriteRegistry = new BuildingSpriteRegistry();

    public InputCommandRouter(
            CoreGameFacade facade,
            BuildModeState buildModeState,
            Consumer<VillageSnapshot> snapshotConsumer,
            Consumer<String> messageConsumer,
            Consumer<CellViewModel> cellConsumer
    ) {
        this.facade = facade;
        this.buildModeState = buildModeState;
        this.snapshotConsumer = snapshotConsumer;
        this.messageConsumer = messageConsumer;
        this.cellConsumer = cellConsumer;
    }

    public void handleGridClick(Position position) {
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
                    buildModeState.clear();
                }
            });
            return;
        }
        try {
            CellViewModel cell = facade.inspectCell(position);
            cellConsumer.accept(cell);
            messageConsumer.accept("Selected " + position);
        } catch (IllegalArgumentException e) {
            messageConsumer.accept(e.getMessage());
        }
        snapshotConsumer.accept(facade.getCurrentSnapshot());
    }
}
