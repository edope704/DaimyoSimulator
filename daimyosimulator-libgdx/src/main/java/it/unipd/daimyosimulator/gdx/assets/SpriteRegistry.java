package it.unipd.daimyosimulator.gdx.assets;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SpriteRegistry {
    public Map<String, String> allSprites() {
        Map<String, String> sprites = new LinkedHashMap<>();
        sprites.put(MissingAssetFallback.NAME, MissingAssetFallback.PATH);
        sprites.put("tile_grass", "assets/textures/tiles/tile_grass.png");
        sprites.put("tile_dirt", "assets/textures/tiles/tile_dirt.png");
        sprites.put("feature_forest", "assets/textures/features/feature_forest.png");
        sprites.put("feature_tree_cluster", "assets/textures/features/feature_tree_cluster.png");
        sprites.put("feature_tree", "assets/textures/features/feature_tree.png");

        for (String name : new String[]{
                "building_dwelling", "building_rice_farm", "building_rice_paddy",
                "building_woodcutters_hut", "building_mine", "building_smithy",
                "building_workshop", "building_market", "building_guard_post", "building_temple"
        }) {
            sprites.put(name, "assets/textures/buildings/" + name + ".png");
        }

        for (String name : new String[]{
                "icon_resource_rice", "icon_resource_timber", "icon_resource_tools",
                "icon_resource_luxury_goods", "icon_parameter_happiness", "icon_parameter_protection",
                "icon_parameter_food", "icon_parameter_faith", "icon_parameter_housing",
                "icon_parameter_craftsmanship", "icon_population", "icon_event_alert",
                "icon_policy_agricultural_expansion", "icon_policy_military_protection",
                "icon_policy_craftsmen_production"
        }) {
            sprites.put(name, "assets/textures/icons/" + name + ".png");
        }

        for (String name : new String[]{
                "panel_parchment", "panel_wood", "panel_large", "button_wood",
                "button_play", "button_pause", "button_fast", "button_faster", "button_close",
                "icon_scroll", "overlay_select_yellow", "overlay_invalid_red", "overlay_valid_blue"
        }) {
            sprites.put(name, "assets/textures/ui/" + name + ".png");
        }
        return sprites;
    }
}
