package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.assets.TileSpriteRegistry;

public final class TileRenderer {
    private final GameAssetManager assetManager;
    private final TileSpriteRegistry tileSpriteRegistry = new TileSpriteRegistry();

    public TileRenderer(GameAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void render(SpriteBatch batch, VillageSnapshot snapshot) {
        var grass = assetManager.getRegion(tileSpriteRegistry.grassTile());
        for (var cell : snapshot.cells()) {
            batch.draw(grass, cell.position().x() * RenderConstants.TILE_SIZE,
                    cell.position().y() * RenderConstants.TILE_SIZE,
                    RenderConstants.TILE_SIZE, RenderConstants.TILE_SIZE);
        }
    }
}
