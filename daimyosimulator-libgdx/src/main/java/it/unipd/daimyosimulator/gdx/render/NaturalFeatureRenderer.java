package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

public final class NaturalFeatureRenderer {
    private final GameAssetManager assetManager;

    public NaturalFeatureRenderer(GameAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void render(SpriteBatch batch, VillageSnapshot snapshot) {
        for (var cell : snapshot.cells()) {
            if (cell.naturalFeature() != null) {
                var region = assetManager.getFeature(cell.naturalFeature());
                drawGroundAnchored(batch, region,
                        cell.position().x() * RenderConstants.TILE_SIZE,
                        cell.position().y() * RenderConstants.TILE_SIZE,
                        1.35f);
            }
        }
    }

    private void drawGroundAnchored(SpriteBatch batch, com.badlogic.gdx.graphics.g2d.TextureRegion region,
                                    float cellX, float cellY, float widthFactor) {
        float width = RenderConstants.TILE_SIZE * widthFactor;
        float height = width * region.getRegionHeight() / region.getRegionWidth();
        float x = cellX + (RenderConstants.TILE_SIZE - width) / 2f;
        float y = cellY + RenderConstants.TILE_SIZE * 0.10f;
        batch.draw(region, x, y, width, height);
    }
}
