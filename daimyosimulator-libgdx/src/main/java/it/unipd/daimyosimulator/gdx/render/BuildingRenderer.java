package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.gdx.assets.BuildingSpriteRegistry;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

public final class BuildingRenderer {
    private final GameAssetManager assetManager;
    private final BuildingSpriteRegistry buildingSpriteRegistry = new BuildingSpriteRegistry();

    public BuildingRenderer(GameAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void render(SpriteBatch batch, VillageSnapshot snapshot) {
        for (var cell : snapshot.cells()) {
            if (cell.building() != null) {
                String sprite = buildingSpriteRegistry.spriteName(cell.building().type());
                batch.draw(assetManager.getRegion(sprite),
                        cell.position().x() * RenderConstants.TILE_SIZE,
                        cell.position().y() * RenderConstants.TILE_SIZE,
                        RenderConstants.TILE_SIZE, RenderConstants.TILE_SIZE);
            }
        }
    }
}
