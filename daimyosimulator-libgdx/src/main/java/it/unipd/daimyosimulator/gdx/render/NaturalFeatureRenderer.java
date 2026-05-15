package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.assets.IconRegistry;

public final class NaturalFeatureRenderer {
    private final GameAssetManager assetManager;
    private final IconRegistry iconRegistry = new IconRegistry();

    public NaturalFeatureRenderer(GameAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void render(SpriteBatch batch, VillageSnapshot snapshot) {
        for (var cell : snapshot.cells()) {
            if (cell.naturalFeature() != null) {
                batch.draw(assetManager.getRegion(iconRegistry.naturalFeature(cell.naturalFeature())),
                        cell.position().x() * RenderConstants.TILE_SIZE,
                        cell.position().y() * RenderConstants.TILE_SIZE,
                        RenderConstants.TILE_SIZE, RenderConstants.TILE_SIZE);
            }
        }
    }
}
