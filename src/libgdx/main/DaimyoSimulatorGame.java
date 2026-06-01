package gdx;

import com.badlogic.gdx.Game;
import gdx.assets.GameAssetManager;
import gdx.screen.LoadingScreen;

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
