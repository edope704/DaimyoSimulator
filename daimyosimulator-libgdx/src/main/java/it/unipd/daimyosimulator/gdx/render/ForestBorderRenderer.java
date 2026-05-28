package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.domain.NaturalFeature;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

/**
 * Renders the 5-tile-wide forest perimeter that surrounds the 20x20 playable grid
 * within the 30x30 render area.  Each tree is drawn exactly like NaturalFeatureRenderer —
 * scaled to fit within its tile cell with no overflow or position jitter.
 */
public final class ForestBorderRenderer {
    private final GameAssetManager assetManager;

    public ForestBorderRenderer(GameAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void render(SpriteBatch batch, VillageSnapshot snapshot) {
        TextureRegion tree = assetManager.getFeature(NaturalFeature.FOREST);
        int ts         = RenderConstants.TILE_SIZE;
        int renderSize = RenderConstants.RENDER_GRID_SIZE;
        int offset     = RenderConstants.PLAYABLE_OFFSET;
        int pw         = snapshot.width();
        int ph         = snapshot.height();

        for (int rx = 0; rx < renderSize; rx++) {
            for (int ry = 0; ry < renderSize; ry++) {
                if (rx >= offset && rx < offset + pw && ry >= offset && ry < offset + ph) continue;
                drawTree(batch, tree, rx, ry, ts);
            }
        }
    }

    private static void drawTree(SpriteBatch batch, TextureRegion tree, int rx, int ry, int ts) {
        float sw = tree.getRegionWidth();
        float sh = tree.getRegionHeight();
        float scale = ts / Math.max(sw, sh);
        float w  = sw * scale;
        float ht = sh * scale;
        float x  = rx * ts + (ts - w)  / 2f;
        float y  = ry * ts + (ts - ht) / 2f;
        batch.draw(tree, x, y, w, ht);
    }
}
