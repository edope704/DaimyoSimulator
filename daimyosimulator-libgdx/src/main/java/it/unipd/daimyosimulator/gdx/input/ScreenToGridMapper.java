package it.unipd.daimyosimulator.gdx.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import it.unipd.daimyosimulator.core.domain.Position;

public final class ScreenToGridMapper {
    public Position gridAt(float worldX, float worldY, int tileSize) {
        return new Position((int) Math.floor(worldX / tileSize), (int) Math.floor(worldY / tileSize));
    }

    public Position screenToGrid(OrthographicCamera camera, int screenX, int screenY, int tileSize) {
        Vector3 world = camera.unproject(new Vector3(screenX, screenY, 0));
        return gridAt(world.x, world.y, tileSize);
    }
}
