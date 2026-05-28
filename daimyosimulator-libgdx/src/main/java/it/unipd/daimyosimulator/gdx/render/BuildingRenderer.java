package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

import java.util.Comparator;

public final class BuildingRenderer {
    private static final int   RICE_ADJACENCY_RANGE = 1;
    private static final float INACTIVE_ALPHA       = 0.70f;

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
                boolean dimmed = cell.building().type() == BuildingType.RICE_PADDY
                        && !hasNearbyFarm(snapshot, cell.position());
                if (dimmed) batch.setColor(0.72f, 0.72f, 0.80f, INACTIVE_ALPHA);
                var region = assetManager.getBuilding(cell.building().type());
                drawGroundAnchored(batch, region,
                        (cell.position().x() + RenderConstants.PLAYABLE_OFFSET) * RenderConstants.TILE_SIZE,
                        (cell.position().y() + RenderConstants.PLAYABLE_OFFSET) * RenderConstants.TILE_SIZE);
                if (dimmed) batch.setColor(1f, 1f, 1f, 1f);
            }
        }
    }

    public void drawPreview(SpriteBatch batch, it.unipd.daimyosimulator.core.building.BuildingType type,
                            int gridX, int gridY) {
        drawGroundAnchored(batch, assetManager.getBuilding(type),
                (gridX + RenderConstants.PLAYABLE_OFFSET) * RenderConstants.TILE_SIZE,
                (gridY + RenderConstants.PLAYABLE_OFFSET) * RenderConstants.TILE_SIZE);
    }

    private boolean hasNearbyFarm(VillageSnapshot snapshot, Position pos) {
        return snapshot.cells().stream().anyMatch(c ->
                c.building() != null
                && c.building().type() == BuildingType.RICE_FARM
                && Math.abs(c.position().x() - pos.x()) <= RICE_ADJACENCY_RANGE
                && Math.abs(c.position().y() - pos.y()) <= RICE_ADJACENCY_RANGE);
    }

    private void drawGroundAnchored(SpriteBatch batch, com.badlogic.gdx.graphics.g2d.TextureRegion region,
                                    float cellX, float cellY) {
        float ts = RenderConstants.TILE_SIZE;
        float sw = region.getRegionWidth();
        float sh = region.getRegionHeight();
        // Scale uniformly so the sprite fits within one tile cell (no horizontal bleed).
        float scale = ts / Math.max(sw, sh);
        float width  = sw * scale;
        float height = sh * scale;
        float x = cellX + (ts - width)  / 2f;
        float y = cellY + (ts - height) / 2f;
        batch.draw(region, x, y, width, height);
    }
}
