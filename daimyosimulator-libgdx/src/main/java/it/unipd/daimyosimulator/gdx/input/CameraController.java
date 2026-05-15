package it.unipd.daimyosimulator.gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;

public final class CameraController extends InputAdapter {
    private static final float PAN_SPEED = 420f;
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 2.5f;
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
        camera.update();
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        camera.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, camera.zoom + amountY * 0.08f));
        camera.update();
        return true;
    }
}
