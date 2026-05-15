package it.unipd.daimyosimulator.gdx.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.NaturalFeature;
import it.unipd.daimyosimulator.core.policy.PolicyType;
import it.unipd.daimyosimulator.core.resource.ResourceType;

public final class GameAssetManager implements Disposable {
    private final SpriteSheetRegionRegistry regionRegistry = new SpriteSheetRegionRegistry();
    private final MissingAssetFallback fallback = new MissingAssetFallback();
    private final BuildingSpriteRegistry buildingSpriteRegistry = new BuildingSpriteRegistry();
    private final TileSpriteRegistry tileSpriteRegistry = new TileSpriteRegistry();
    private final FeatureSpriteRegistry featureSpriteRegistry = new FeatureSpriteRegistry();
    private final IconRegistry iconRegistry = new IconRegistry();
    private final UiTextureRegistry uiTextureRegistry = new UiTextureRegistry();

    public void loadAssets() {
        regionRegistry.load();
    }

    public TextureRegion getRegion(String name) {
        TextureRegion region = regionRegistry.get(name);
        if (region == null) {
            if (Gdx.app != null) {
                Gdx.app.log("DaimyoSimulator", "Missing sprite: " + name);
            }
            return regionRegistry.get(fallback.fallbackName());
        }
        return region;
    }

    public TextureRegion getBuilding(BuildingType type) {
        return getRegion(buildingSpriteRegistry.spriteName(type));
    }

    public TextureRegion getFeature(NaturalFeature feature) {
        return getRegion(featureSpriteRegistry.spriteName(feature));
    }

    public TextureRegion getTile(TileType type) {
        return getRegion(tileSpriteRegistry.spriteName(type));
    }

    public TextureRegion getResourceIcon(ResourceType type) {
        return getRegion(iconRegistry.resource(type));
    }

    public TextureRegion getParameterIcon(ParameterType type) {
        return getRegion(iconRegistry.parameter(type));
    }

    public TextureRegion getPolicyIcon(PolicyType type) {
        return getRegion(iconRegistry.policy(type));
    }

    public TextureRegion getUi(String key) {
        return getRegion(key);
    }

    public UiTextureRegistry ui() {
        return uiTextureRegistry;
    }

    @Override
    public void dispose() {
        regionRegistry.dispose();
    }
}
