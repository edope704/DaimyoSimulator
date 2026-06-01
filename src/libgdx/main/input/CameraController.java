package gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import gdx.render.RenderConstants;

public final class CameraController extends InputAdapter {
    private static final float PAN_SPEED = 420f;
    private static final float MIN_ZOOM  = 0.4f;
    private final OrthographicCamera camera;

    public CameraController(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void update(float delta) {
        float movement = PAN_SPEED * delta * camera.zoom;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= movement;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += movement;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.y += movement;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= movement;
        }
        clampZoom();
        clampPosition();
        camera.update();
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        camera.zoom += amountY * 0.08f;
        clampZoom();
        clampPosition();
        camera.update();
        return true;
    }

    /** Prevent zooming out beyond the point where the void outside the map becomes visible. */
    private void clampZoom() {
        float worldSize = RenderConstants.RENDER_GRID_SIZE * (float) RenderConstants.TILE_SIZE;
        // Max zoom = map fills the shorter viewport dimension (no void in that axis).
        float maxZoom = worldSize / Math.max(camera.viewportWidth, camera.viewportHeight);
        camera.zoom = Math.max(MIN_ZOOM, Math.min(maxZoom, camera.zoom));
    }

    /** Clamp camera so it never scrolls past the map edges. Centers when viewport > map. */
    private void clampPosition() {
        float worldSize = RenderConstants.RENDER_GRID_SIZE * (float) RenderConstants.TILE_SIZE;
        float halfW = camera.viewportWidth  * camera.zoom / 2f;
        float halfH = camera.viewportHeight * camera.zoom / 2f;
        // When halfW/halfH > worldSize/2, center camera on map (can't avoid overflow in that axis).
        float minX = halfW,         maxX = Math.max(halfW, worldSize - halfW);
        float minY = halfH,         maxY = Math.max(halfH, worldSize - halfH);
        camera.position.x = Math.max(minX, Math.min(maxX, camera.position.x));
        camera.position.y = Math.max(minY, Math.min(maxY, camera.position.y));
    }
}
