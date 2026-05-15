package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.assets.TileType;

public final class TileRenderer {
    private final GameAssetManager assetManager;

    public TileRenderer(GameAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void render(SpriteBatch batch, VillageSnapshot snapshot) {
        for (var cell : snapshot.cells()) {
            TileType tile = ((cell.position().x() + cell.position().y()) % 7 == 0) ? TileType.DIRT : TileType.GRASS;
            batch.draw(assetManager.getTile(tile), cell.position().x() * RenderConstants.TILE_SIZE,
                    cell.position().y() * RenderConstants.TILE_SIZE,
                    RenderConstants.TILE_SIZE, RenderConstants.TILE_SIZE);
        }
    }
}
