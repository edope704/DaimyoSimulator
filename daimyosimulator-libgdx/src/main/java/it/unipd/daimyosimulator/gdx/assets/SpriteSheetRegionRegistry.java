package it.unipd.daimyosimulator.gdx.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loads named sprite textures from individual pre-cut PNG files under
 * assets/textures/sprites/.  One file per key, no post-processing needed.
 */
public final class SpriteSheetRegionRegistry implements Disposable {
    private static final String SPRITES_DIR = "assets/textures/sprites/";

    private static final String[] KEYS = {
        "tile_grass",
        "tile_dirt",
        "overlay_select_yellow",
        "overlay_invalid_red",
        "overlay_valid_blue",
        "feature_forest",
        "building_dwelling",
        "building_rice_farm",
        "building_rice_paddy",
        "building_woodcutters_hut",
        "building_mine",
        "building_smithy",
        "building_workshop",
        "building_market",
        "building_guard_post",
        "building_temple",
        "icon_resource_rice",
        "icon_resource_timber",
        "icon_resource_tools",
        "icon_resource_luxury_goods",
        "icon_parameter_happiness",
        "icon_parameter_protection",
        "icon_parameter_food",
        "icon_parameter_faith",
        "icon_parameter_housing",
        "icon_parameter_craftsmanship",
        "icon_population",
        "icon_event_alert",
        "settings_icon",
        "sound_icon",
        "question_icon",
        "icon_policy_agricultural_expansion",
        "icon_policy_military_protection",
        "icon_policy_craftsmen_production",
        "button_play",
        "button_pause",
        "button_fast",
        "button_faster",
        "button_close",
        "icon_scroll",
        "missing_asset",
    };

    private final Map<String, TextureRegion> regions  = new LinkedHashMap<>();
    private final Map<String, Texture>       textures = new LinkedHashMap<>();

    public void load() {
        for (String key : KEYS) {
            String path = SPRITES_DIR + key + ".png";
            Texture texture = new Texture(Gdx.files.internal(path));
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            textures.put(key, texture);
            regions.put(key, new TextureRegion(texture));
        }
    }

    public TextureRegion get(String key) {
        return regions.get(key);
    }

    public Map<String, TextureRegion> regions() {
        return Map.copyOf(regions);
    }

    @Override
    public void dispose() {
        textures.values().forEach(Texture::dispose);
        textures.clear();
        regions.clear();
    }
}
