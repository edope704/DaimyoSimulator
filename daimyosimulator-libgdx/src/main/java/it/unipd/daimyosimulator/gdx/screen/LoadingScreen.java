package it.unipd.daimyosimulator.gdx.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import it.unipd.daimyosimulator.gdx.DaimyoSimulatorGame;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

public final class LoadingScreen extends ScreenAdapter {
    private final DaimyoSimulatorGame game;
    private final GameAssetManager assetManager;
    private boolean loaded;

    public LoadingScreen(DaimyoSimulatorGame game, GameAssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.09f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!loaded) {
            assetManager.loadAssets();
            loaded = true;
            game.setScreen(new MainMenuScreen(game, assetManager));
        }
    }
}
