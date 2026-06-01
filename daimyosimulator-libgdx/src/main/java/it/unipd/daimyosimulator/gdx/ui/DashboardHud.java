package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.result.TickResult;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.app.view.DashboardViewModel;
import it.unipd.daimyosimulator.core.app.view.EventLogViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.assets.GameSoundManager;
import it.unipd.daimyosimulator.gdx.input.BuildModeState;

import java.util.function.Consumer;

public final class DashboardHud extends Table {
    // 4.5 on-screen forest tiles wide at the default zoom: a tile is
    // 1280 / (RENDER_GRID_SIZE * 0.85) ≈ 50.2 stage units, so 4.5 * 50.2 ≈ 226.
    private static final float LEFT_PANEL_WIDTH = 226f;

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

    private final GameSoundManager soundManager;

    public DashboardHud(Skin skin, GameAssetManager assetManager, CoreGameFacade facade,
                        BuildModeState buildModeState, GameSoundManager soundManager) {
        this.skin = skin;
        this.facade = facade;
        this.soundManager = soundManager;
        this.buildMenu            = new BuildMenu(skin, assetManager, buildModeState, this::setStatus, soundManager);
        this.resourcePanel        = new ResourcePanel(skin, assetManager);
        this.populationPanel      = new PopulationPanel(skin, assetManager);
        this.parameterPanel       = new VillageParameterPanel(skin, assetManager);
        this.selectedBuildingPanel = new SelectedBuildingPanel(skin, assetManager, this::openMarket);
        this.eventLogPanel        = new EventLogPanel(skin, assetManager);
        this.speedControlPanel    = new SpeedControlPanel(skin, assetManager, this::nextTick, soundManager);
        this.policyPanel          = new PolicyPanel(skin, assetManager, facade, this::setStatus,
                () -> refresh(facade.getCurrentSnapshot(), facade.getDashboard()));
        this.warningPanel         = new WarningPanel(skin);

        // ── Top bar icon buttons ──────────────────────────────────────────────
        Button.ButtonStyle iconBtnStyle = new Button.ButtonStyle();
        iconBtnStyle.up   = skin.getDrawable("hud-panel");
        iconBtnStyle.down = skin.getDrawable("hud-panel-light");
        iconBtnStyle.over = skin.getDrawable("hud-panel-light");

        Button gearBtn = new Button(iconBtnStyle);
        gearBtn.add(new Image(skin.getDrawable("icon-settings"))).size(34);
        gearBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                soundManager.playClick();
                openGameMenu();
            }
        });

        Button soundBtn = new Button(iconBtnStyle);
        soundBtn.add(new Image(skin.getDrawable("icon-sound"))).size(34);
        soundBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                soundManager.playClick();
                openSettings();
            }
        });

        Button helpBtn = new Button(iconBtnStyle);
        helpBtn.add(new Image(skin.getDrawable("icon-question"))).size(34);
        helpBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                soundManager.playClick();
                openTutorial();
            }
        });

        Table top = new Table();
        // Left group: speed controls — no padLeft so its left edge lines up with the
        // build menu below (both end up at x=6 via the surrounding pads).
        top.add(speedControlPanel);
        // Spacer — pushes center content away from speed controls
        top.add(new Table()).expandX();
        // Center content: resources + population
        top.add(resourcePanel).padRight(16);
        top.add(populationPanel).padRight(16);
        // Spacer — pushes icon buttons to the right edge
        top.add(new Table()).expandX();
        // Right group: icon buttons
        top.add(gearBtn).size(48).padLeft(4);
        top.add(soundBtn).size(48).padLeft(4);
        top.add(helpBtn).size(48).padLeft(4);

        // ── Left side column: BuildMenu (top) + PolicyPanel (below) ───────────
        Table leftCol = new Table();
        leftCol.top().left();
        leftCol.add(buildMenu).width(LEFT_PANEL_WIDTH).left();
        leftCol.row().padTop(6);
        leftCol.add(policyPanel).width(LEFT_PANEL_WIDTH).left().fillX();

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
        bottom.add(selectedBuildingPanel).width(LEFT_PANEL_WIDTH).left().padRight(8);

        // ── Root layout ───────────────────────────────────────────────────────
        setFillParent(true);
        top().left();
        add(top).expandX().height(64).fillX().left().top().pad(6);
        row();

        Table middle = new Table();
        middle.add(leftCol).width(LEFT_PANEL_WIDTH).expandY().left().top().padLeft(6);
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
        EventModal.dismissAll(getStage());
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

    private void openGameMenu() {
        if (getStage() != null) {
            new SettingsDialog(skin, facade,
                    snapshot -> { snapshotConsumer.accept(snapshot); refresh(snapshot, facade.getDashboard()); },
                    this::setStatus, soundManager).show(getStage());
            getStage().cancelTouchFocus();
        }
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

    private void openSettings() {
        if (getStage() != null) {
            new AudioSettingsDialog(skin, soundManager).show(getStage());
            getStage().cancelTouchFocus();
        }
    }
}
