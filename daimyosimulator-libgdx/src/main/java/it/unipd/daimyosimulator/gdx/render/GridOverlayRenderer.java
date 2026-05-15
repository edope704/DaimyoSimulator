package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.input.BuildModeState;

public final class GridOverlayRenderer implements Disposable {
    private final SpriteBatch batch = new SpriteBatch();
    private final GameAssetManager assetManager;

    public GridOverlayRenderer(GameAssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void render(OrthographicCamera camera, VillageSnapshot snapshot, BuildModeState buildModeState, Position selected) {
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
    }

    private void drawOverlay(String key, Position position) {
        batch.draw(assetManager.getUi(key), position.x() * RenderConstants.TILE_SIZE,
                position.y() * RenderConstants.TILE_SIZE, RenderConstants.TILE_SIZE, RenderConstants.TILE_SIZE);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
