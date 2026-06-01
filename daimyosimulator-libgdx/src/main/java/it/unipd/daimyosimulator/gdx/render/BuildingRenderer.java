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
    private static final int   MINE_ADJACENCY_RANGE = 1;
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
                boolean dimmed = isDimmed(cell, snapshot);
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

    private boolean isDimmed(CellViewModel cell, VillageSnapshot snapshot) {
        return switch (cell.building().type()) {
            case RICE_PADDY -> !hasNearby(snapshot, cell.position(), BuildingType.RICE_FARM, RICE_ADJACENCY_RANGE);
            case SMITHY, WORKSHOP -> !hasNearby(snapshot, cell.position(), BuildingType.MINE, MINE_ADJACENCY_RANGE);
            default -> false;
        };
    }

    private boolean hasNearby(VillageSnapshot snapshot, Position pos, BuildingType target, int range) {
        return snapshot.cells().stream().anyMatch(c ->
                c.building() != null
                && c.building().type() == target
                && Math.abs(c.position().x() - pos.x()) <= range
                && Math.abs(c.position().y() - pos.y()) <= range);
    }

    private void drawGroundAnchored(SpriteBatch batch, com.badlogic.gdx.graphics.g2d.TextureRegion region,
                                    float cellX, float cellY) {
        float ts = RenderConstants.TILE_SIZE;
        float sw = region.getRegionWidth();
        float sh = region.getRegionHeight();
        float scale = ts / Math.max(sw, sh);
        float width  = sw * scale;
        float height = sh * scale;
        float x = cellX + (ts - width)  / 2f;
        float y = cellY + (ts - height) / 2f;
        batch.draw(region, x, y, width, height);
    }
}
