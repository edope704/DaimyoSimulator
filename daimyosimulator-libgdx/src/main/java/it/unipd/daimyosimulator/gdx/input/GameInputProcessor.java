package it.unipd.daimyosimulator.gdx.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import it.unipd.daimyosimulator.gdx.render.RenderConstants;

public final class GameInputProcessor extends InputAdapter {
    private final OrthographicCamera camera;
    private final ScreenToGridMapper mapper;
    private final InputCommandRouter commandRouter;

    public GameInputProcessor(OrthographicCamera camera, ScreenToGridMapper mapper, InputCommandRouter commandRouter) {
        this.camera = camera;
        this.mapper = mapper;
        this.commandRouter = commandRouter;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) {
            return false;
        }
        commandRouter.handleGridClick(mapper.screenToGrid(camera, screenX, screenY, RenderConstants.TILE_SIZE));
        return true;
    }
}
