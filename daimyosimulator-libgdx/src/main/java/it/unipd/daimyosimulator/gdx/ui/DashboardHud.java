package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.app.view.DashboardViewModel;
import it.unipd.daimyosimulator.core.app.view.EventLogViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.input.BuildModeState;

import java.util.function.Consumer;

public final class DashboardHud extends Table {
    private final CoreGameFacade facade;
    private final ResourcePanel resourcePanel;
    private final PopulationPanel populationPanel;
    private final VillageParameterPanel parameterPanel;
    private final SelectedBuildingPanel selectedBuildingPanel;
    private final EventLogPanel eventLogPanel;
    private final PolicyPanel policyPanel;
    private final SpeedControlPanel speedControlPanel;
    private Consumer<VillageSnapshot> snapshotConsumer = snapshot -> { };
    private Position selectedPosition;

    public DashboardHud(Skin skin, GameAssetManager assetManager, CoreGameFacade facade, BuildModeState buildModeState) {
        this.facade = facade;
        setBackground(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(assetManager.getUi(assetManager.ui().panelWood())));
        this.resourcePanel = new ResourcePanel(skin, assetManager);
        this.populationPanel = new PopulationPanel(skin, assetManager);
        this.parameterPanel = new VillageParameterPanel(skin, assetManager);
        this.selectedBuildingPanel = new SelectedBuildingPanel(skin, assetManager);
        this.eventLogPanel = new EventLogPanel(skin, assetManager);
        this.speedControlPanel = new SpeedControlPanel(skin, assetManager, this::nextTick);
        this.policyPanel = new PolicyPanel(skin, assetManager, facade, this::setStatus, () -> refresh(facade.getCurrentSnapshot(), facade.getDashboard()));

        Table top = new Table();
        top.left();
        top.add(new MenuOverlay(skin, assetManager, facade, snapshot -> {
            snapshotConsumer.accept(snapshot);
            refresh(snapshot, facade.getDashboard());
        }, this::setStatus)).left().padRight(8);
        top.add(resourcePanel).left().padRight(12);
        top.add(populationPanel).left().padRight(12);
        top.add(speedControlPanel).left();

        Table side = new Table();
        side.top().left();
        side.add(new BuildMenu(skin, assetManager, buildModeState, this::setStatus)).left();
        side.row().padTop(8);
        side.add(policyPanel).left();
        side.row().padTop(8);
        side.add(parameterPanel).left();
        side.row().padTop(8);
        side.add(selectedBuildingPanel).left();
        side.row().padTop(8);
        side.add(eventLogPanel).left();

        setFillParent(true);
        top().left();
        add(top).expandX().fillX().left().pad(6);
        row();
        add(side).left().top().pad(6);
    }

    public void setSnapshotConsumer(Consumer<VillageSnapshot> snapshotConsumer) {
        this.snapshotConsumer = snapshotConsumer;
    }

    public void refresh(VillageSnapshot snapshot, DashboardViewModel dashboard) {
        resourcePanel.refresh(dashboard.resources());
        populationPanel.refresh(dashboard.population());
        parameterPanel.refresh(dashboard.parameters());
        policyPanel.refresh(dashboard.policy());
        eventLogPanel.refresh(new EventLogViewModel(snapshot.latestEvents()));
    }

    public void setStatus(String status) {
        eventLogPanel.addStatus(status);
    }

    public void setSelectedCell(CellViewModel cell) {
        selectedBuildingPanel.refresh(cell);
        selectedPosition = cell == null ? null : cell.position();
    }

    public Position getSelectedPosition() {
        return selectedPosition;
    }

    public boolean isPaused() {
        return speedControlPanel.isPaused();
    }

    public int getSpeedMultiplier() {
        return speedControlPanel.getSpeedMultiplier();
    }

    private void nextTick() {
        var result = facade.advanceTick();
        String status = result.messages().isEmpty() ? "Tick " + result.afterState().tick() : String.join(" | ", result.messages());
        setStatus(status);
        snapshotConsumer.accept(result.afterState());
        refresh(result.afterState(), facade.getDashboard());
    }
}
