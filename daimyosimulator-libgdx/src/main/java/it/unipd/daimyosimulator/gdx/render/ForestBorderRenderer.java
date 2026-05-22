package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.domain.NaturalFeature;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

/**
 * Renders a dense forest ring around the 20×20 playable grid.
 * Uses the same feature_forest sprite as in-grid trees; positioned in a
 * 3-cell deep border on all four sides.  Positions and scales are varied
 * deterministically via a tiny integer hash so adjacent trees don't look
 * identical without needing any external assets.
 */
public final class ForestBorderRenderer {
    private static final int   BORDER_DEPTH      = 3;
    private static final float BASE_SCALE        = 1.25f;
    private static final float SCALE_VARIATION   = 0.30f;
    private static final float Y_OFFSET_FRACTION = 0.12f;

    private final GameAssetManager assetManager;

    public ForestBorderRenderer(GameAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void render(SpriteBatch batch, VillageSnapshot snapshot) {
        TextureRegion tree = assetManager.getFeature(NaturalFeature.FOREST);

        int gridW = snapshot.width();
        int gridH = snapshot.height();
        int ts    = RenderConstants.TILE_SIZE;

        int xMin = -BORDER_DEPTH;
        int xMax = gridW + BORDER_DEPTH;
        int yMin = -BORDER_DEPTH;
        int yMax = gridH + BORDER_DEPTH;

        for (int cx = xMin; cx < xMax; cx++) {
            for (int cy = yMin; cy < yMax; cy++) {
                if (cx >= 0 && cx < gridW && cy >= 0 && cy < gridH) continue;
                drawBorderTree(batch, tree, cx, cy, ts);
            }
        }
        batch.setColor(Color.WHITE);
    }

    private void drawBorderTree(SpriteBatch batch, TextureRegion tree, int cx, int cy, int ts) {
        int h = hash(cx, cy);

        float scaleFactor = BASE_SCALE + ((h & 0xFF) / 255f - 0.5f) * SCALE_VARIATION;
        float w  = ts * scaleFactor;
        float ht = w * tree.getRegionHeight() / (float) tree.getRegionWidth();

        float px    = cx * ts + (ts - w) / 2f + ((h >> 8 & 0xF) - 8) * 1.5f;
        float py    = cy * ts + ts * Y_OFFSET_FRACTION;
        float alpha = 0.80f + ((h >> 16 & 0xF) / 15f) * 0.20f;

        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(tree, px, py, w, ht);
    }

    private static int hash(int x, int y) {
        int h = x * 0x9E3779B9 ^ y * 0x6C62272E;
        h ^= h >>> 16;
        h *= 0x45D9F3B;
        h ^= h >>> 16;
        return h;
    }
}
