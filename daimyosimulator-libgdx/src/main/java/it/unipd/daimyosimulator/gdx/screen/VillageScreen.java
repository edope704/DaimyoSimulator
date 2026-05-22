package it.unipd.daimyosimulator.gdx.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.gdx.DaimyoSimulatorGame;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.input.*;
import it.unipd.daimyosimulator.gdx.render.RenderConstants;
import it.unipd.daimyosimulator.gdx.render.WorldRenderer;
import it.unipd.daimyosimulator.gdx.ui.DashboardHud;
import it.unipd.daimyosimulator.gdx.ui.EventModal;
import it.unipd.daimyosimulator.gdx.ui.HudSkinFactory;
import it.unipd.daimyosimulator.gdx.ui.TutorialDialog;

public final class VillageScreen extends ScreenAdapter {
    private final DaimyoSimulatorGame game;
    private final GameAssetManager assetManager;
    private final CoreGameFacade facade;
    private final BuildModeState buildModeState = new BuildModeState();
    private final boolean showTutorialOnStart;
    private VillageSnapshot currentSnapshot;
    private WorldRenderer worldRenderer;
    private OrthographicCamera camera;
    private CameraController cameraController;
    private Stage stage;
    private Skin skin;
    private DashboardHud hud;
    private float autoTickTimer;
    private boolean debugOverlay;
    private boolean tutorialShown = false;

    /** Called from MainMenuScreen when starting a brand-new village. */
    public VillageScreen(DaimyoSimulatorGame game, GameAssetManager assetManager, boolean showTutorial) {
        this(game, assetManager, showTutorial, new CoreGameFacade());
    }

    /** Called when the caller already owns a loaded facade (e.g. Load from main menu). */
    public VillageScreen(DaimyoSimulatorGame game, GameAssetManager assetManager,
                         boolean showTutorial, CoreGameFacade facade) {
        this.game = game;
        this.assetManager = assetManager;
        this.facade = facade;
        this.showTutorialOnStart = showTutorial;
    }

    /** Legacy constructor (no tutorial; used internally or from tests). */
    public VillageScreen(DaimyoSimulatorGame game, GameAssetManager assetManager) {
        this(game, assetManager, false);
    }

    @Override
    public void show() {
        currentSnapshot = facade.getCurrentSnapshot();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(currentSnapshot.width() * RenderConstants.TILE_SIZE / 2f,
                currentSnapshot.height() * RenderConstants.TILE_SIZE / 2f, 0);
        camera.update();
        cameraController = new CameraController(camera);
        worldRenderer = new WorldRenderer(assetManager);

        stage = new Stage(new ScreenViewport());
        skin  = new HudSkinFactory().create(assetManager);
        hud   = new DashboardHud(skin, assetManager, facade, buildModeState);
        hud.setSnapshotConsumer(this::setSnapshot);
        hud.refresh(currentSnapshot, facade.getDashboard());
        stage.addActor(hud);

        InputCommandRouter router = new InputCommandRouter(
                facade, buildModeState, this::setSnapshot,
                hud::setStatus, hud::setSelectedCell);
        GameInputProcessor gameInputProcessor =
                new GameInputProcessor(camera, new ScreenToGridMapper(), router, buildModeState);
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, cameraController, gameInputProcessor));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.12f, 0.17f, 0.13f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugOverlay = !debugOverlay;
            hud.setStatus("Debug overlay " + (debugOverlay ? "on" : "off"));
        }

        cameraController.update(delta);
        processAutomaticTicks(delta);
        worldRenderer.render(currentSnapshot, camera, buildModeState,
                hud.getSelectedPosition(), delta, debugOverlay);
        stage.act(delta);
        stage.draw();

        // Auto-show tutorial once after the first frame is painted.
        if (showTutorialOnStart && !tutorialShown) {
            tutorialShown = true;
            new TutorialDialog(skin).show(stage);
        }
    }

    private void processAutomaticTicks(float delta) {
        if (hud.isPaused()) {
            autoTickTimer = 0;
            return;
        }
        autoTickTimer += delta;
        float interval = 1.2f / hud.getSpeedMultiplier();
        if (autoTickTimer >= interval) {
            autoTickTimer = 0;
            var result = facade.advanceTick();
            setSnapshot(result.afterState());
            hud.refreshAfterTick(result);
            String status = result.messages().isEmpty()
                    ? "Tick " + result.afterState().tick()
                    : String.join(" | ", result.messages());
            hud.setStatus(status);

            // Show event modal if random events fired this tick.
            EventModal.showIfAny(skin, result.randomEventReports(), stage);
        }
    }

    private void setSnapshot(VillageSnapshot snapshot) {
        currentSnapshot = snapshot;
        if (hud != null) {
            hud.refresh(snapshot, facade.getDashboard());
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.viewportWidth  = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        if (worldRenderer != null) worldRenderer.dispose();
        if (stage != null) stage.dispose();
        if (skin  != null) skin.dispose();
    }
}
