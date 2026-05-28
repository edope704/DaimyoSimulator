package it.unipd.daimyosimulator.gdx.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import it.unipd.daimyosimulator.gdx.render.RenderConstants;

public final class GameInputProcessor extends InputAdapter {
    private final OrthographicCamera camera;
    private final ScreenToGridMapper mapper;
    private final InputCommandRouter commandRouter;
    private final BuildModeState buildModeState;

    public GameInputProcessor(OrthographicCamera camera, ScreenToGridMapper mapper,
                              InputCommandRouter commandRouter, BuildModeState buildModeState) {
        this.camera = camera;
        this.mapper = mapper;
        this.commandRouter = commandRouter;
        this.buildModeState = buildModeState;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            commandRouter.handleCancel();
            return true;
        }
        if (button != Input.Buttons.LEFT) {
            return false;
        }
        var pos = mapper.screenToGrid(camera, screenX, screenY, RenderConstants.TILE_SIZE);
        int gridSize = RenderConstants.RENDER_GRID_SIZE - 2 * RenderConstants.PLAYABLE_OFFSET;
        if (pos.x() < 0 || pos.y() < 0 || pos.x() >= gridSize || pos.y() >= gridSize) {
            return true; // border tile – swallow click silently
        }
        commandRouter.handleGridClick(pos);
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            commandRouter.handleCancel();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        buildModeState.setPreviewPosition(mapper.screenToGrid(camera, screenX, screenY, RenderConstants.TILE_SIZE));
        return false;
    }
}
