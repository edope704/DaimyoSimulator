package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public final class UiViewportFactory {
    private static final float BASE_WIDTH = 1280f;
    private static final float BASE_HEIGHT = 720f;
    private static final float DEFAULT_UI_SCALE = 0.85f;

    private UiViewportFactory() {
    }

    public static ScreenViewport create() {
        ScreenViewport viewport = new ScreenViewport();
        update(viewport, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return viewport;
    }

    public static void update(Stage stage, int width, int height) {
        update((ScreenViewport) stage.getViewport(), width, height);
    }

    private static void update(ScreenViewport viewport, int width, int height) {
        viewport.setUnitsPerPixel(1f / scaleFor(width, height));
        viewport.update(width, height, true);
    }

    private static float scaleFor(int width, int height) {
        if (width <= 0 || height <= 0) {
            return 1f;
        }
        return Math.min(width / BASE_WIDTH, height / BASE_HEIGHT) * DEFAULT_UI_SCALE;
    }
}
