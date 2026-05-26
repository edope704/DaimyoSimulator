package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.result.TickResult;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.app.view.DashboardViewModel;
import it.unipd.daimyosimulator.core.app.view.EventLogViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.input.BuildModeState;

import java.util.function.Consumer;

public final class DashboardHud extends Table {
    private final Skin skin;
    private final CoreGameFacade facade;
    private final BuildMenu buildMenu;
    private final ResourcePanel resourcePanel;
    private final PopulationPanel populationPanel;
    private final VillageParameterPanel parameterPanel;
    private final SelectedBuildingPanel selectedBuildingPanel;
    private final EventLogPanel eventLogPanel;
    private final PolicyPanel policyPanel;
    private final SpeedControlPanel speedControlPanel;
    private final WarningPanel warningPanel;
    private Consumer<VillageSnapshot> snapshotConsumer = snapshot -> { };
    private Runnable onManualTickCallback = () -> { };
    private Position selectedPosition;

    public DashboardHud(Skin skin, GameAssetManager assetManager, CoreGameFacade facade,
                        BuildModeState buildModeState) {
        this.skin = skin;
        this.facade = facade;
        this.buildMenu            = new BuildMenu(skin, assetManager, buildModeState, this::setStatus);
        this.resourcePanel        = new ResourcePanel(skin, assetManager);
        this.populationPanel      = new PopulationPanel(skin, assetManager);
        this.parameterPanel       = new VillageParameterPanel(skin, assetManager);
        this.selectedBuildingPanel = new SelectedBuildingPanel(skin, assetManager, this::openMarket);
        this.eventLogPanel        = new EventLogPanel(skin, assetManager);
        this.speedControlPanel    = new SpeedControlPanel(skin, assetManager, this::nextTick);
        this.policyPanel          = new PolicyPanel(skin, assetManager, facade, this::setStatus,
                () -> refresh(facade.getCurrentSnapshot(), facade.getDashboard()));
        this.warningPanel         = new WarningPanel(skin);

        // ── Top bar ───────────────────────────────────────────────────────────
        TextButton helpButton = new TextButton("?", skin);
        helpButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                openTutorial();
            }
        });

        TextButton cmdButton = new TextButton("Cmd", skin);
        cmdButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                openCommands();
            }
        });

        Table top = new Table();
        top.left();
        top.add(new MenuOverlay(skin, assetManager, facade,
                snapshot -> { snapshotConsumer.accept(snapshot); refresh(snapshot, facade.getDashboard()); },
                this::setStatus)).left().padRight(8);
        top.add(resourcePanel).left().padRight(12);
        top.add(populationPanel).left().padRight(12);
        top.add(speedControlPanel).left().padRight(8);
        top.add(cmdButton).size(42).padRight(4);
        top.add(helpButton).size(36).right();

        // ── Left side column: BuildMenu (top) + PolicyPanel (below) ───────────
        Table leftCol = new Table();
        leftCol.top().left();
        leftCol.add(buildMenu).left();
        leftCol.row().padTop(6);
        leftCol.add(policyPanel).left().fillX();

        // ── Right side: WarningPanel + parameters + event log ────────────────
        Table rightCol = new Table();
        rightCol.top().right();
        rightCol.add(warningPanel).right().width(172).padBottom(6);
        rightCol.row();
        rightCol.add(parameterPanel).right();
        rightCol.row().padTop(6);
        rightCol.add(eventLogPanel).right().fillX();

        // ── Bottom bar ────────────────────────────────────────────────────────
        Table bottom = new Table();
        bottom.left();
        bottom.add(selectedBuildingPanel).left().padRight(8);

        // ── Root layout ───────────────────────────────────────────────────────
        setFillParent(true);
        top().left();
        add(top).expandX().height(58).fillX().left().top().pad(6);
        row();

        Table middle = new Table();
        middle.add(leftCol).width(200).expandY().left().top().padLeft(6);
        middle.add(new Table()).expandX().expand();       // empty game viewport
        middle.add(rightCol).width(180).expandY().right().top().padRight(6);

        add(middle).expandX().expandY().fillX().fillY();
        row();
        add(bottom).expandX().height(88).fillX().left().bottom().padLeft(6).padRight(6).padTop(6).padBottom(6);
    }

    public void setSnapshotConsumer(Consumer<VillageSnapshot> snapshotConsumer) {
        this.snapshotConsumer = snapshotConsumer;
    }

    public void setOnManualTickCallback(Runnable callback) {
        this.onManualTickCallback = callback;
    }

    public void refresh(VillageSnapshot snapshot, DashboardViewModel dashboard) {
        resourcePanel.refresh(dashboard.resources());
        populationPanel.refresh(dashboard.population());
        parameterPanel.refresh(dashboard.parameters());
        policyPanel.refresh(dashboard.policy());
        eventLogPanel.refresh(new EventLogViewModel(snapshot.latestEvents()));
        buildMenu.refresh(snapshot);
        speedControlPanel.updateTick(snapshot.tick());
    }

    /** Called after each tick advance; updates resource deltas and all panels. */
    public void refreshAfterTick(TickResult result) {
        resourcePanel.refreshWithDelta(
                result.afterState().resources(),
                result.producedResources(),
                result.consumedResources());
        buildMenu.refresh(result.afterState());
        populationPanel.refresh(result.afterState().population());
        parameterPanel.refresh(result.afterState().parameters());
        policyPanel.refresh(result.afterState().policy());
        eventLogPanel.refresh(new EventLogViewModel(result.afterState().latestEvents()));
        warningPanel.onTick(result.afterState().resources());
        speedControlPanel.updateTick(result.afterState().tick());
    }

    public void setStatus(String status) {
        eventLogPanel.addStatus(status);
    }

    public void setSelectedCell(CellViewModel cell) {
        selectedBuildingPanel.refresh(cell, facade.getCurrentSnapshot());
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

    public void updateTickProgress(float fraction) {
        speedControlPanel.updateProgress(Math.max(0f, Math.min(1f, fraction)));
    }

    private void nextTick() {
        var result = facade.advanceTick();
        String status = result.messages().isEmpty()
                ? "Tick " + result.afterState().tick()
                : String.join(" | ", result.messages());
        setStatus(status);
        snapshotConsumer.accept(result.afterState());
        refreshAfterTick(result);
        onManualTickCallback.run();
        EventModal.showIfAny(skin, result.randomEventReports(), getStage());
    }

    private void openMarket(CellViewModel cell) {
        if (getStage() != null) {
            MarketDialog dialog = new MarketDialog(skin, facade, this::setStatus,
                    () -> refresh(facade.getCurrentSnapshot(), facade.getDashboard()));
            dialog.show(getStage());
            getStage().cancelTouchFocus();
        }
    }

    private void openTutorial() {
        if (getStage() != null) {
            new TutorialDialog(skin).show(getStage());
            getStage().cancelTouchFocus();
        }
    }

    private void openCommands() {
        if (getStage() != null) {
            new CommandsDialog(skin).show(getStage());
            getStage().cancelTouchFocus();
        }
    }
}
