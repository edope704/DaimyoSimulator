package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.input.BuildModeState;

public final class GridOverlayRenderer implements Disposable {
    private static final float HOVER_ALPHA        = 0.50f; // build/demolish preview
    private static final float IDLE_HOVER_ALPHA   = 0.28f; // general mouse hover
    private static final float BORDER_WIDTH       = 3f;

    private final SpriteBatch batch = new SpriteBatch();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final GameAssetManager assetManager;

    public GridOverlayRenderer(GameAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void render(OrthographicCamera camera, VillageSnapshot snapshot, BuildModeState buildModeState,
                       Position selected, boolean debug) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (buildModeState.isActive() && buildModeState.previewPosition().isPresent()) {
            // Build or demolish preview – semi-transparent so tile is visible.
            Position preview = buildModeState.previewPosition().orElseThrow();
            boolean isDemolish = buildModeState.isDemolishMode();
            boolean placementValid = !isDemolish && buildModeState.lastPlacementValid().orElse(true);
            String overlay = placementValid
                    ? assetManager.ui().validOverlay()
                    : assetManager.ui().invalidOverlay();
            // Tint the dashed-border sprite green for valid, red for invalid/demolish.
            if (placementValid) {
                batch.setColor(0.30f, 1.0f, 0.30f, HOVER_ALPHA);
            } else {
                batch.setColor(1.0f, 0.30f, 0.30f, HOVER_ALPHA);
            }
            drawOverlay(overlay, preview);
        } else if (!buildModeState.isActive() && buildModeState.previewPosition().isPresent()) {
            // General hover (no build mode) – subtle blue tint so player can see tile graphics.
            Position hover = buildModeState.previewPosition().orElseThrow();
            // Only draw hover when the position is different from the selected cell.
            if (!hover.equals(selected) && isInsideGrid(hover, snapshot)) {
                batch.setColor(0.55f, 0.85f, 1.0f, IDLE_HOVER_ALPHA);
                drawOverlay(assetManager.ui().validOverlay(), hover);
            }
        }

        batch.setColor(Color.WHITE); // reset
        batch.end();

        renderGridBorder(camera, snapshot);

        if (debug) {
            renderDebug(camera, snapshot);
        }
    }

    private static boolean isInsideGrid(Position p, VillageSnapshot snapshot) {
        return p != null && p.x() >= 0 && p.x() < snapshot.width()
                && p.y() >= 0 && p.y() < snapshot.height();
    }

    private void drawOverlay(String key, Position position) {
        batch.draw(assetManager.getUi(key),
                position.x() * RenderConstants.TILE_SIZE,
                position.y() * RenderConstants.TILE_SIZE,
                RenderConstants.TILE_SIZE, RenderConstants.TILE_SIZE);
    }

    /** Draws a visible frame around the whole 20×20 grid. */
    private void renderGridBorder(OrthographicCamera camera, VillageSnapshot snapshot) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.25f, 0.20f, 0.12f, 1f);
        float w = snapshot.width()  * RenderConstants.TILE_SIZE;
        float h = snapshot.height() * RenderConstants.TILE_SIZE;
        // Bottom
        shapeRenderer.rect(-BORDER_WIDTH, -BORDER_WIDTH, w + BORDER_WIDTH * 2, BORDER_WIDTH);
        // Top
        shapeRenderer.rect(-BORDER_WIDTH, h, w + BORDER_WIDTH * 2, BORDER_WIDTH);
        // Left
        shapeRenderer.rect(-BORDER_WIDTH, 0, BORDER_WIDTH, h);
        // Right
        shapeRenderer.rect(w, 0, BORDER_WIDTH, h);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
    }

    private void renderDebug(OrthographicCamera camera, VillageSnapshot snapshot) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 0.35f);
        for (int x = 0; x <= snapshot.width(); x++) {
            float px = x * RenderConstants.TILE_SIZE;
            shapeRenderer.line(px, 0, px, snapshot.height() * RenderConstants.TILE_SIZE);
        }
        for (int y = 0; y <= snapshot.height(); y++) {
            float py = y * RenderConstants.TILE_SIZE;
            shapeRenderer.line(0, py, snapshot.width() * RenderConstants.TILE_SIZE, py);
        }
        shapeRenderer.setColor(1f, 0.85f, 0.1f, 0.8f);
        for (var cell : snapshot.cells()) {
            if (cell.building() != null) {
                shapeRenderer.rect(cell.position().x() * RenderConstants.TILE_SIZE,
                        cell.position().y() * RenderConstants.TILE_SIZE,
                        RenderConstants.TILE_SIZE, RenderConstants.TILE_SIZE);
            }
        }
        shapeRenderer.end();
    }
}
