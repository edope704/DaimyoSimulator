package gdx.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import core.app.CoreGameFacade;
import gdx.DaimyoSimulatorGame;
import gdx.assets.GameAssetManager;
import gdx.ui.AudioSettingsDialog;
import gdx.ui.HudSkinFactory;
import gdx.ui.SaveLoadDialog;
import gdx.ui.UiViewportFactory;

public final class MainMenuScreen extends ScreenAdapter {
    private final DaimyoSimulatorGame game;
    private final GameAssetManager assetManager;
    private final CoreGameFacade facade;
    private Stage stage;
    private Skin skin;

    public MainMenuScreen(DaimyoSimulatorGame game, GameAssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
        this.facade = new CoreGameFacade();
    }

    @Override
    public void show() {
        stage = new Stage(UiViewportFactory.create());
        skin  = new HudSkinFactory().create(assetManager);

        // ── Big title with a scaled font ────────────────────────────────────
        BitmapFont bigFont = new BitmapFont();
        bigFont.getData().setScale(2.6f);
        skin.add("big-font", bigFont);

        Label.LabelStyle titleStyle = new Label.LabelStyle(bigFont, new Color(0.98f, 0.82f, 0.35f, 1f));
        skin.add("main-title", titleStyle);

        Label.LabelStyle subtitleStyle = new Label.LabelStyle(new BitmapFont(), new Color(0.72f, 0.65f, 0.45f, 1f));
        skin.add("subtitle", subtitleStyle);

        // ── Layout ────────────────────────────────────────────────────────────
        Table root = new Table();
        root.setFillParent(true);

        // Top half: title pushed high.
        Table topSection = new Table();
        topSection.center();
        topSection.add(new Label("DAIMYO SIMULATOR", skin, "main-title")).padBottom(6);
        topSection.row();
        topSection.add(new Label("Build - Grow - Survive", skin, "subtitle")).padBottom(48);

        root.add(topSection).expandX().expandY().top().padTop(80);
        root.row();

        // Buttons centred in lower half.
        Table buttons = new Table();
        buttons.center();

        TextButton newGame = new TextButton("New Game", skin);
        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new VillageScreen(game, assetManager, true));
            }
        });
        buttons.add(newGame).width(200).height(44).padBottom(12);
        buttons.row();

        TextButton loadGame = new TextButton("Load Game", skin);
        loadGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                new SaveLoadDialog(skin, facade, false,
                        snapshot -> {
                            // facade already holds the loaded village; pass it to VillageScreen.
                            game.setScreen(new VillageScreen(game, assetManager, false, facade));
                        },
                        msg -> { /* no status bar on main menu */ }).show(stage);
            }
        });
        buttons.add(loadGame).width(200).height(44).padBottom(12);
        buttons.row();

        TextButton settings = new TextButton("Settings", skin);
        settings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                new AudioSettingsDialog(skin, null).show(stage);
            }
        });
        buttons.add(settings).width(200).height(44).padBottom(12);
        buttons.row();

        root.add(buttons).expandX().expandY().bottom().padBottom(100);
        root.row();

        stage.addActor(root);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.06f, 0.09f, 0.07f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        UiViewportFactory.update(stage, width, height);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (skin  != null) skin.dispose();
    }
}
