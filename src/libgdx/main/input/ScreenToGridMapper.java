package gdx.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import core.domain.Position;
import gdx.render.RenderConstants;

public final class ScreenToGridMapper {
    public Position gridAt(float worldX, float worldY, int tileSize) {
        int offset = RenderConstants.PLAYABLE_OFFSET;
        return new Position(
                (int) Math.floor(worldX / tileSize) - offset,
                (int) Math.floor(worldY / tileSize) - offset);
    }

    public Position screenToGrid(OrthographicCamera camera, int screenX, int screenY, int tileSize) {
        Vector3 world = camera.unproject(new Vector3(screenX, screenY, 0));
        return gridAt(world.x, world.y, tileSize);
    }
}
