package it.unipd.daimyosimulator.gdx.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import it.unipd.daimyosimulator.gdx.DaimyoSimulatorGame;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.ui.HudSkinFactory;

public final class MainMenuScreen extends ScreenAdapter {
    private final DaimyoSimulatorGame game;
    private final GameAssetManager assetManager;
    private Stage stage;
    private Skin skin;

    public MainMenuScreen(DaimyoSimulatorGame game, GameAssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new HudSkinFactory().create(assetManager);
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(new Label("DaimyoSimulator", skin)).padBottom(16);
        root.row();
        TextButton start = new TextButton("Start Village", skin);
        start.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new VillageScreen(game, assetManager));
            }
        });
        root.add(start).width(180).height(36);
        stage.addActor(root);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.11f, 0.09f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
    }
}
