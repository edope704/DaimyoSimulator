package gdx.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import core.app.view.VillageSnapshot;
import gdx.assets.GameAssetManager;
import gdx.assets.TileType;

public final class TileRenderer {
    private final GameAssetManager assetManager;

    public TileRenderer(GameAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void render(SpriteBatch batch, VillageSnapshot snapshot) {
        int ts = RenderConstants.TILE_SIZE;
        int offset = RenderConstants.PLAYABLE_OFFSET;
        int renderSize = RenderConstants.RENDER_GRID_SIZE;
        int pw = snapshot.width();
        int ph = snapshot.height();

        // Border tiles (outside playable area)
        for (int rx = 0; rx < renderSize; rx++) {
            for (int ry = 0; ry < renderSize; ry++) {
                if (rx >= offset && rx < offset + pw && ry >= offset && ry < offset + ph) continue;
                batch.draw(assetManager.getTile(tileAt(rx - offset, ry - offset)),
                        rx * ts, ry * ts, ts, ts);
            }
        }

        // Playable grid cells
        for (var cell : snapshot.cells()) {
            int x = cell.position().x();
            int y = cell.position().y();
            batch.draw(assetManager.getTile(tileAt(x, y)),
                    (x + offset) * ts,
                    (y + offset) * ts,
                    ts, ts);
        }
    }

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
