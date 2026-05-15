package it.unipd.daimyosimulator.gdx.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

public final class GameAssetManager implements Disposable {
    private final Map<String, TextureRegion> regions = new HashMap<>();
    private final Map<String, Texture> textures = new HashMap<>();
    private final MissingAssetFallback fallback = new MissingAssetFallback();

    public void loadGeneratedPlaceholders() {
        put(MissingAssetFallback.NAME, MissingAssetFallback.COLOR, Color.BLACK);
        put("tile_grass", new Color(0.34f, 0.62f, 0.30f, 1f), new Color(0.25f, 0.46f, 0.22f, 1f));
        put("tile_dirt", new Color(0.59f, 0.42f, 0.23f, 1f), new Color(0.45f, 0.31f, 0.16f, 1f));
        put("feature_forest", new Color(0.07f, 0.36f, 0.16f, 1f), new Color(0.02f, 0.18f, 0.08f, 1f));
        put("building_dwelling", new Color(0.74f, 0.50f, 0.22f, 1f), new Color(0.35f, 0.20f, 0.10f, 1f));
        put("building_rice_farm", new Color(0.84f, 0.64f, 0.30f, 1f), new Color(0.35f, 0.20f, 0.10f, 1f));
        put("building_rice_paddy", new Color(0.26f, 0.62f, 0.78f, 1f), Color.GREEN);
        put("building_woodcutters_hut", new Color(0.52f, 0.34f, 0.16f, 1f), new Color(0.35f, 0.20f, 0.10f, 1f));
        put("building_mine", new Color(0.40f, 0.40f, 0.40f, 1f), Color.DARK_GRAY);
        put("building_smithy", new Color(0.20f, 0.20f, 0.25f, 1f), Color.ORANGE);
        put("building_workshop", new Color(0.28f, 0.24f, 0.20f, 1f), new Color(0.95f, 0.74f, 0.22f, 1f));
        put("building_market", new Color(0.25f, 0.45f, 0.78f, 1f), Color.SKY);
        put("building_guard_post", new Color(0.70f, 0.55f, 0.25f, 1f), new Color(0.04f, 0.12f, 0.32f, 1f));
        put("building_temple", new Color(0.60f, 0.10f, 0.10f, 1f), Color.LIGHT_GRAY);
        put("ui_button", new Color(0.20f, 0.16f, 0.11f, 1f), new Color(0.48f, 0.36f, 0.20f, 1f));
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

    private void put(String name, Color fill, Color mark) {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(fill);
        pixmap.fill();
        pixmap.setColor(mark);
        pixmap.drawRectangle(1, 1, 30, 30);
        pixmap.drawLine(4, 24, 16, 8);
        pixmap.drawLine(16, 8, 28, 24);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
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
