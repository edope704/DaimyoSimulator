package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

import java.util.Comparator;

public final class BuildingRenderer {
    private final GameAssetManager assetManager;

    public BuildingRenderer(GameAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void render(SpriteBatch batch, VillageSnapshot snapshot) {
        for (var cell : snapshot.cells().stream()
                .filter(candidate -> candidate.building() != null)
                .sorted(Comparator.comparingInt((CellViewModel candidate) -> candidate.position().y())
                        .thenComparingInt(candidate -> candidate.position().x()))
                .toList()) {
            if (cell.building() != null) {
                var region = assetManager.getBuilding(cell.building().type());
                drawGroundAnchored(batch, region,
                        cell.position().x() * RenderConstants.TILE_SIZE,
                        cell.position().y() * RenderConstants.TILE_SIZE,
                        1.25f);
            }
        }
    }

    public void drawPreview(SpriteBatch batch, it.unipd.daimyosimulator.core.building.BuildingType type,
                            int gridX, int gridY) {
        drawGroundAnchored(batch, assetManager.getBuilding(type),
                gridX * RenderConstants.TILE_SIZE, gridY * RenderConstants.TILE_SIZE, 1.25f);
    }

    private void drawGroundAnchored(SpriteBatch batch, com.badlogic.gdx.graphics.g2d.TextureRegion region,
                                    float cellX, float cellY, float widthFactor) {
        float width = RenderConstants.TILE_SIZE * widthFactor;
        float height = width * region.getRegionHeight() / region.getRegionWidth();
        float x = cellX + (RenderConstants.TILE_SIZE - width) / 2f;
        float y = cellY + RenderConstants.TILE_SIZE * 0.08f;
        batch.draw(region, x, y, width, height);
    }
}
