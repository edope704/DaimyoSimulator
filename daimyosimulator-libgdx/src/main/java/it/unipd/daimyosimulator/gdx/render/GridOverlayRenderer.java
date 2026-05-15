package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.input.BuildModeState;

public final class GridOverlayRenderer implements Disposable {
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    public void render(OrthographicCamera camera, VillageSnapshot snapshot, BuildModeState buildModeState, Position selected) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0f, 0f, 0f, 0.35f);
        for (int x = 0; x <= snapshot.width(); x++) {
            float px = x * RenderConstants.TILE_SIZE;
            shapeRenderer.line(px, 0, px, snapshot.height() * RenderConstants.TILE_SIZE);
        }
        for (int y = 0; y <= snapshot.height(); y++) {
            float py = y * RenderConstants.TILE_SIZE;
            shapeRenderer.line(0, py, snapshot.width() * RenderConstants.TILE_SIZE, py);
        }
        if (selected != null) {
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(selected.x() * RenderConstants.TILE_SIZE, selected.y() * RenderConstants.TILE_SIZE,
                    RenderConstants.TILE_SIZE, RenderConstants.TILE_SIZE);
        }
        if (buildModeState.isActive()) {
            shapeRenderer.setColor(Color.CYAN);
            shapeRenderer.rect(0, 0, snapshot.width() * RenderConstants.TILE_SIZE,
                    snapshot.height() * RenderConstants.TILE_SIZE);
        }
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
