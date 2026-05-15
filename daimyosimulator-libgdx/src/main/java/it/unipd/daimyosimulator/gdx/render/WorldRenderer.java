package it.unipd.daimyosimulator.gdx.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.input.BuildModeState;

public final class WorldRenderer implements Disposable {
    private final SpriteBatch batch = new SpriteBatch();
    private final TileRenderer tileRenderer;
    private final NaturalFeatureRenderer naturalFeatureRenderer;
    private final BuildingRenderer buildingRenderer;
    private final AnimationRenderer animationRenderer = new AnimationRenderer();
    private final GridOverlayRenderer gridOverlayRenderer = new GridOverlayRenderer();

    public WorldRenderer(GameAssetManager assetManager) {
        this.tileRenderer = new TileRenderer(assetManager);
        this.naturalFeatureRenderer = new NaturalFeatureRenderer(assetManager);
        this.buildingRenderer = new BuildingRenderer(assetManager);
    }

    public void render(VillageSnapshot snapshot, OrthographicCamera camera, BuildModeState buildModeState,
                       Position selected, float delta) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        tileRenderer.render(batch, snapshot);
        naturalFeatureRenderer.render(batch, snapshot);
        buildingRenderer.render(batch, snapshot);
        animationRenderer.render(batch, snapshot, delta);
        batch.end();
        gridOverlayRenderer.render(camera, snapshot, buildModeState, selected);
    }

    @Override
    public void dispose() {
        gridOverlayRenderer.dispose();
        batch.dispose();
    }
}
