package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.input.BuildModeState;

public final class GridOverlayRenderer implements Disposable {
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
        if (selected != null) {
            drawOverlay(assetManager.ui().selectedOverlay(), selected);
        }
        if (buildModeState.isActive() && buildModeState.previewPosition().isPresent()) {
            Position preview = buildModeState.previewPosition().orElseThrow();
            String overlay = buildModeState.lastPlacementValid()
                    .map(valid -> valid ? assetManager.ui().validOverlay() : assetManager.ui().invalidOverlay())
                    .orElse(assetManager.ui().validOverlay());
            drawOverlay(overlay, preview);
        }
        batch.end();
        if (debug) {
            renderDebug(camera, snapshot);
        }
    }

    private void drawOverlay(String key, Position position) {
        batch.draw(assetManager.getUi(key), position.x() * RenderConstants.TILE_SIZE,
                position.y() * RenderConstants.TILE_SIZE, RenderConstants.TILE_SIZE, RenderConstants.TILE_SIZE);
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
