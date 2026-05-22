package it.unipd.daimyosimulator.gdx.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SpriteSheetRegionRegistry implements Disposable {
    public static final String SOURCE_PATH = "assets/textures/source/Textures.png";

    private record RegionDefinition(String key, int x, int y, int width, int height, boolean transparentEdges) {
    }

    private static final RegionDefinition[] DEFINITIONS = {
            new RegionDefinition("tile_grass", 17, 45, 103, 116, true),
            new RegionDefinition("tile_dirt", 143, 45, 101, 116, true),
            new RegionDefinition("overlay_select_yellow", 267, 42, 98, 118, true),
            new RegionDefinition("overlay_invalid_red", 385, 42, 100, 118, true),
            new RegionDefinition("overlay_valid_blue", 505, 42, 100, 118, true),
            new RegionDefinition("feature_forest", 617, 24, 179, 145, true),

            new RegionDefinition("building_dwelling", 9, 207, 115, 137, true),
            new RegionDefinition("building_rice_farm", 139, 208, 119, 136, true),
            new RegionDefinition("building_rice_paddy", 283, 210, 105, 133, true),
            new RegionDefinition("building_woodcutters_hut", 145, 400, 123, 135, true),
            new RegionDefinition("building_mine", 523, 209, 105, 132, true),
            new RegionDefinition("building_smithy", 641, 204, 103, 137, true),
            new RegionDefinition("building_workshop", 766, 207, 98, 134, true),
            new RegionDefinition("building_market", 879, 205, 112, 137, true),
            new RegionDefinition("building_guard_post", 678, 394, 117, 147, true),
            new RegionDefinition("building_temple", 812, 393, 151, 149, true),

            new RegionDefinition("icon_resource_rice", 21, 586, 88, 83, true),
            new RegionDefinition("icon_resource_timber", 143, 589, 98, 76, true),
            new RegionDefinition("icon_resource_tools", 278, 587, 82, 80, true),
            new RegionDefinition("icon_resource_luxury_goods", 409, 590, 100, 76, true),
            new RegionDefinition("icon_parameter_happiness", 20, 857, 49, 49, true),
            new RegionDefinition("icon_parameter_protection", 80, 857, 49, 49, true),
            new RegionDefinition("icon_parameter_food", 140, 857, 49, 49, true),
            new RegionDefinition("icon_parameter_faith", 202, 857, 49, 49, true),
            new RegionDefinition("icon_parameter_housing", 263, 857, 49, 49, true),
            new RegionDefinition("icon_parameter_craftsmanship", 323, 857, 49, 49, true),
            new RegionDefinition("icon_population", 22, 720, 78, 84, true),
            new RegionDefinition("icon_event_alert", 906, 1104, 52, 63, true),
            new RegionDefinition("icon_policy_agricultural_expansion", 557, 586, 91, 82, true),
            new RegionDefinition("icon_policy_military_protection", 698, 586, 83, 80, true),
            new RegionDefinition("icon_policy_craftsmen_production", 823, 588, 83, 78, true),

            new RegionDefinition("button_play", 17, 1101, 56, 69, true),
            new RegionDefinition("button_pause", 80, 1101, 56, 69, true),
            new RegionDefinition("button_fast", 143, 1101, 56, 69, true),
            new RegionDefinition("button_faster", 206, 1101, 56, 69, true),
            new RegionDefinition("button_close", 755, 1102, 56, 68, true),
            new RegionDefinition("icon_scroll", 692, 1103, 56, 67, true),
            new RegionDefinition("missing_asset", 16, 1360, 76, 103, false)
    };

    private final Map<String, TextureRegion> regions = new LinkedHashMap<>();
    private final Map<String, Texture> textures = new LinkedHashMap<>();

    public void load() {
        FileHandle source = Gdx.files.internal(SOURCE_PATH);
        Pixmap sheet = new Pixmap(source);
        try {
            for (RegionDefinition definition : DEFINITIONS) {
                register(sheet, definition);
            }
        } finally {
            sheet.dispose();
        }
    }

    public TextureRegion get(String key) {
        return regions.get(key);
    }

    public Map<String, TextureRegion> regions() {
        return Map.copyOf(regions);
    }

    private void register(Pixmap sheet, RegionDefinition definition) {
        Pixmap crop = new Pixmap(definition.width, definition.height, Pixmap.Format.RGBA8888);
        crop.drawPixmap(sheet, definition.x, definition.y, definition.width, definition.height,
                0, 0, definition.width, definition.height);
        if (definition.transparentEdges) {
            clearConnectedCheckerboard(crop);
        }
        Texture texture = new Texture(crop);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        crop.dispose();
        textures.put(definition.key, texture);
        regions.put(definition.key, new TextureRegion(texture));
        if (Gdx.app != null) {
            Gdx.app.log("DaimyoSimulator", "REGION " + definition.key
                    + " x=" + definition.x + " y=" + definition.y
                    + " w=" + definition.width + " h=" + definition.height);
        }
    }

    private void clearConnectedCheckerboard(Pixmap pixmap) {
        int width = pixmap.getWidth();
        int height = pixmap.getHeight();
        boolean[][] visited = new boolean[height][width];
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        for (int x = 0; x < width; x++) {
            enqueueBackground(pixmap, visited, queue, x, 0);
            enqueueBackground(pixmap, visited, queue, x, height - 1);
        }
        for (int y = 0; y < height; y++) {
            enqueueBackground(pixmap, visited, queue, 0, y);
            enqueueBackground(pixmap, visited, queue, width - 1, y);
        }
        while (!queue.isEmpty()) {
            int[] point = queue.removeFirst();
            int x = point[0];
            int y = point[1];
            pixmap.drawPixel(x, y, 0);
            enqueueBackground(pixmap, visited, queue, x + 1, y);
            enqueueBackground(pixmap, visited, queue, x - 1, y);
            enqueueBackground(pixmap, visited, queue, x, y + 1);
            enqueueBackground(pixmap, visited, queue, x, y - 1);
        }
    }

    private void enqueueBackground(Pixmap pixmap, boolean[][] visited, ArrayDeque<int[]> queue, int x, int y) {
        if (x < 0 || y < 0 || x >= pixmap.getWidth() || y >= pixmap.getHeight() || visited[y][x]) {
            return;
        }
        visited[y][x] = true;
        if (isCheckerboardBackground(pixmap.getPixel(x, y))) {
            queue.addLast(new int[]{x, y});
        }
    }

    private boolean isCheckerboardBackground(int rgba8888) {
        int red = (rgba8888 >>> 24) & 0xff;
        int green = (rgba8888 >>> 16) & 0xff;
        int blue = (rgba8888 >>> 8) & 0xff;
        int alpha = rgba8888 & 0xff;
        int max = Math.max(red, Math.max(green, blue));
        int min = Math.min(red, Math.min(green, blue));
        // Fully transparent → background.
        if (alpha == 0) return true;
        // Near-white solid (original white sprite-sheet background).
        if (min >= 220 && max - min <= 20) return true;
        // Mid-gray checkerboard squares (Photoshop/GIMP style: 170-205 range, near-neutral).
        if (min >= 155 && max <= 215 && max - min <= 25 && alpha >= 240) return true;
        // Semi-transparent near-white anti-aliasing fringe.
        if (min >= 200 && max - min <= 25 && alpha < 220) return true;
        return false;
    }

    @Override
    public void dispose() {
        textures.values().forEach(Texture::dispose);
        textures.clear();
        regions.clear();
    }
}
