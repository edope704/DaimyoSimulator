package it.unipd.daimyosimulator.gdx;

import com.badlogic.gdx.Game;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.screen.LoadingScreen;

public final class DaimyoSimulatorGame extends Game {
    private GameAssetManager assetManager;

    @Override
    public void create() {
        assetManager = new GameAssetManager();
        setScreen(new LoadingScreen(this, assetManager));
    }

    public GameAssetManager getGameAssetManager() {
        return assetManager;
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        if (assetManager != null) {
            assetManager.dispose();
        }
    }
}
