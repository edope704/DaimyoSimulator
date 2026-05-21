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
            int x = cell.position().x();
            int y = cell.position().y();
            TileType tile = tileAt(x, y);
            batch.draw(assetManager.getTile(tile),
                    x * RenderConstants.TILE_SIZE,
                    y * RenderConstants.TILE_SIZE,
                    RenderConstants.TILE_SIZE, RenderConstants.TILE_SIZE);
        }
    }

    /**
     * Uses a fast integer hash of (x,y) to scatter dirt tiles aperiodically.
     * ~12 % of cells become dirt, forming small natural-looking patches rather
     * than the visually obvious diagonal stripe of the original modulo-7 rule.
     */
    private static TileType tileAt(int x, int y) {
        int h = hash(x, y);
        return ((h & 0xFF) < 30) ? TileType.DIRT : TileType.GRASS;
    }

    private static int hash(int x, int y) {
        int h = x * 0x9E3779B9 ^ y * 0x6C62272E;
        h ^= h >>> 16;
        h *= 0x45D9F3B;
        h ^= h >>> 16;
        return h;
    }
}
