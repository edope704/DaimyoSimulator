package it.unipd.daimyosimulator.gdx.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.domain.NaturalFeature;
import it.unipd.daimyosimulator.core.policy.PolicyType;
import it.unipd.daimyosimulator.core.resource.ResourceType;

import java.util.HashMap;
import java.util.Map;

public final class GameAssetManager implements Disposable {
    private final Map<String, TextureRegion> regions = new HashMap<>();
    private final Map<String, Texture> textures = new HashMap<>();
    private final MissingAssetFallback fallback = new MissingAssetFallback();
    private final SpriteRegistry spriteRegistry = new SpriteRegistry();
    private final BuildingSpriteRegistry buildingSpriteRegistry = new BuildingSpriteRegistry();
    private final TileSpriteRegistry tileSpriteRegistry = new TileSpriteRegistry();
    private final FeatureSpriteRegistry featureSpriteRegistry = new FeatureSpriteRegistry();
    private final IconRegistry iconRegistry = new IconRegistry();
    private final UiTextureRegistry uiTextureRegistry = new UiTextureRegistry();

    public void loadAssets() {
        for (Map.Entry<String, String> entry : spriteRegistry.allSprites().entrySet()) {
            load(entry.getKey(), entry.getValue());
        }
    }

    public TextureRegion getRegion(String name) {
        TextureRegion region = regions.get(name);
        if (region == null) {
            if (Gdx.app != null) {
                Gdx.app.log("DaimyoSimulator", "Missing sprite: " + name);
            }
            return regions.get(fallback.fallbackName());
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

    private void load(String name, String path) {
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) {
            if (Gdx.app != null) {
                Gdx.app.error("DaimyoSimulator", "Missing sprite file for key " + name + ": " + path);
            }
            return;
        }
        Texture texture = new Texture(file);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        textures.put(name, texture);
        regions.put(name, new TextureRegion(texture));
    }

    @Override
    public void dispose() {
        textures.values().forEach(Texture::dispose);
        textures.clear();
        regions.clear();
    }
}
