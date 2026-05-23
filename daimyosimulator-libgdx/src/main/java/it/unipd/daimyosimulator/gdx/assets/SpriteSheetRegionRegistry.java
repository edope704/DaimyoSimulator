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

/**
 * Loads named sprite regions from the mixed (non-uniform) spritesheet.
 *
 * Each RegionDefinition supplies a *search area* on the sheet – the rectangle
 * that is known to contain the sprite.  The actual extracted crop is tighter:
 *
 *   1. The first non-background pixel inside the search area is used as a BFS
 *      seed.
 *   2. An 8-directional BFS spreads through all non-background pixels that are
 *      connected to the seed, tracking the tight bounding box.
 *   3. A second scan of that bounding box widens it to include sprite pixels
 *      that are disconnected from the seed by transparent holes (windows, gaps).
 *   4. The tight crop is extracted from the sheet.
 *   5. A flood-fill from every edge pixel of the crop removes any remaining
 *      white / checkerboard background that is edge-connected.
 *
 * The result is a texture containing only the sprite content with a fully
 * transparent background – no white frames, no checkerboard artifacts.
 */
public final class SpriteSheetRegionRegistry implements Disposable {
    public static final String SOURCE_PATH = "assets/textures/source/Textures.png";

    /**
     * Search-area hints.  x/y/width/height define the rectangle of the sheet
     * that is guaranteed to contain (and mostly surround) the named sprite.
     * The BFS step auto-detects the actual tight bounds inside that rectangle.
     */
    private record RegionDefinition(String key, int x, int y, int width, int height,
                                    boolean removeBg) {}

    private static final RegionDefinition[] DEFINITIONS = {
            new RegionDefinition("tile_grass",               17,   45,  103, 116, true),
            new RegionDefinition("tile_dirt",               143,   45,  101, 116, true),
            new RegionDefinition("overlay_select_yellow",   267,   42,   98, 118, true),
            new RegionDefinition("overlay_invalid_red",     385,   42,  100, 118, true),
            new RegionDefinition("overlay_valid_blue",      505,   42,  100, 118, true),
            new RegionDefinition("feature_forest",          617,   24,  179, 145, true),

            new RegionDefinition("building_dwelling",         9,  207,  115, 137, true),
            new RegionDefinition("building_rice_farm",      139,  208,  119, 136, true),
            new RegionDefinition("building_rice_paddy",     283,  210,  105, 133, true),
            new RegionDefinition("building_woodcutters_hut",145,  400,  123, 135, true),
            new RegionDefinition("building_mine",           523,  209,  105, 132, true),
            new RegionDefinition("building_smithy",         641,  204,  103, 137, true),
            new RegionDefinition("building_workshop",       766,  207,   98, 134, true),
            new RegionDefinition("building_market",         879,  205,  112, 137, true),
            new RegionDefinition("building_guard_post",     678,  394,  117, 147, true),
            new RegionDefinition("building_temple",         812,  393,  151, 149, true),

            new RegionDefinition("icon_resource_rice",       21,  586,   88,  83, true),
            new RegionDefinition("icon_resource_timber",    143,  589,   98,  76, true),
            new RegionDefinition("icon_resource_tools",     278,  587,   82,  80, true),
            new RegionDefinition("icon_resource_luxury_goods",409, 590, 100,  76, true),
            new RegionDefinition("icon_parameter_happiness",  20, 857,   49,  49, true),
            new RegionDefinition("icon_parameter_protection", 80, 857,   49,  49, true),
            new RegionDefinition("icon_parameter_food",      140, 857,   49,  49, true),
            new RegionDefinition("icon_parameter_faith",     202, 857,   49,  49, true),
            new RegionDefinition("icon_parameter_housing",   263, 857,   49,  49, true),
            new RegionDefinition("icon_parameter_craftsmanship",323,857, 49,  49, true),
            new RegionDefinition("icon_population",           22, 720,   78,  84, true),
            new RegionDefinition("icon_event_alert",         906,1104,   52,  63, true),
            new RegionDefinition("icon_policy_agricultural_expansion",557,586,91,82,true),
            new RegionDefinition("icon_policy_military_protection",  698,586,83,80,true),
            new RegionDefinition("icon_policy_craftsmen_production", 823,588,83,78,true),

            new RegionDefinition("button_play",               17, 1101,  56,  69, true),
            new RegionDefinition("button_pause",              80, 1101,  56,  69, true),
            new RegionDefinition("button_fast",              143, 1101,  56,  69, true),
            new RegionDefinition("button_faster",            206, 1101,  56,  69, true),
            new RegionDefinition("button_close",             755, 1102,  56,  68, true),
            new RegionDefinition("icon_scroll",              692, 1103,  56,  67, true),
            new RegionDefinition("missing_asset",             16, 1360,  76, 103, false),
    };

    private final Map<String, TextureRegion> regions  = new LinkedHashMap<>();
    private final Map<String, Texture>       textures = new LinkedHashMap<>();

    public void load() {
        FileHandle source = Gdx.files.internal(SOURCE_PATH);
        Pixmap sheet = new Pixmap(source);
        try {
            for (RegionDefinition def : DEFINITIONS) {
                register(sheet, def);
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

    // ── Registration ──────────────────────────────────────────────────────────

    private void register(Pixmap sheet, RegionDefinition def) {
        int cx, cy, cw, ch;
        if (def.removeBg()) {
            int[] tight = findTightBounds(sheet, def);
            if (tight != null) {
                cx = tight[0]; cy = tight[1]; cw = tight[2]; ch = tight[3];
            } else {
                cx = def.x(); cy = def.y(); cw = def.width(); ch = def.height();
            }
        } else {
            cx = def.x(); cy = def.y(); cw = def.width(); ch = def.height();
        }

        Pixmap crop = new Pixmap(cw, ch, Pixmap.Format.RGBA8888);
        crop.drawPixmap(sheet, cx, cy, cw, ch, 0, 0, cw, ch);
        if (def.removeBg()) {
            clearEdgeConnectedBackground(crop);
        }

        Texture texture = new Texture(crop);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        crop.dispose();
        textures.put(def.key(), texture);
        regions.put(def.key(), new TextureRegion(texture));

        if (Gdx.app != null) {
            Gdx.app.log("DaimyoSimulator",
                    "REGION " + def.key() + " tight=(" + cx + "," + cy + " " + cw + "×" + ch + ")");
        }
    }

    // ── BFS tight-bounds detection ────────────────────────────────────────────

    /**
     * Returns the tight bounding box [x, y, w, h] of all sprite pixels inside
     * the search area defined by {@code def}.
     *
     * Algorithm:
     * <ol>
     *   <li>Scan the search area top-left → bottom-right for the first
     *       non-background pixel (the BFS seed).</li>
     *   <li>8-directional BFS from the seed through non-background pixels,
     *       constrained to the search area.  Track min/max x,y.</li>
     *   <li>One additional scan of the found bounding box to widen it with any
     *       sprite pixels that are disconnected from the seed by transparent
     *       holes (e.g. windows, gaps inside building sprites).</li>
     * </ol>
     */
    private int[] findTightBounds(Pixmap sheet, RegionDefinition def) {
        int sheetW = sheet.getWidth();
        int sheetH = sheet.getHeight();
        int rx0 = def.x(), ry0 = def.y();
        int rx1 = Math.min(def.x() + def.width(),  sheetW);
        int ry1 = Math.min(def.y() + def.height(), sheetH);

        // ── Step 1: find first non-background pixel (seed) ───────────────────
        int seedX = -1, seedY = -1;
        outer:
        for (int py = ry0; py < ry1; py++) {
            for (int px = rx0; px < rx1; px++) {
                if (!isBackground(sheet.getPixel(px, py))) {
                    seedX = px; seedY = py;
                    break outer;
                }
            }
        }
        if (seedX < 0) return null; // entire region is background

        // ── Step 2: 8-directional BFS through non-background pixels ──────────
        int regW = rx1 - rx0;
        int regH = ry1 - ry0;
        boolean[][] visited = new boolean[regH][regW];
        ArrayDeque<int[]> queue = new ArrayDeque<>();

        visited[seedY - ry0][seedX - rx0] = true;
        queue.add(new int[]{seedX, seedY});

        int bx1 = seedX, by1 = seedY, bx2 = seedX, by2 = seedY;

        while (!queue.isEmpty()) {
            int[] p = queue.poll();
            int px = p[0], py = p[1];
            if (px < bx1) bx1 = px; if (px > bx2) bx2 = px;
            if (py < by1) by1 = py; if (py > by2) by2 = py;

            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (dx == 0 && dy == 0) continue;
                    int nx = px + dx, ny = py + dy;
                    if (nx < rx0 || nx >= rx1 || ny < ry0 || ny >= ry1) continue;
                    int lx = nx - rx0, ly = ny - ry0;
                    if (visited[ly][lx]) continue;
                    visited[ly][lx] = true;
                    if (!isBackground(sheet.getPixel(nx, ny))) {
                        queue.add(new int[]{nx, ny});
                    }
                }
            }
        }

        // ── Step 3: expand bounding box for disconnected sprite pixels ────────
        // Pixels separated from the seed by transparent holes are not reached by
        // the 8-directional BFS.  A single scan of the entire search area catches
        // any such isolated sprite pixels and widens the bounding box to include them.
        for (int py = ry0; py < ry1; py++) {
            for (int px = rx0; px < rx1; px++) {
                if (!isBackground(sheet.getPixel(px, py))) {
                    if (px < bx1) bx1 = px; if (px > bx2) bx2 = px;
                    if (py < by1) by1 = py; if (py > by2) by2 = py;
                }
            }
        }

        return new int[]{bx1, by1, bx2 - bx1 + 1, by2 - by1 + 1};
    }

    // ── Edge-connected background removal ─────────────────────────────────────

    /**
     * Flood-fills from every edge pixel of the crop, clearing all background
     * pixels that are edge-connected to the border (white / checkerboard /
     * semi-transparent fringe).  Interior transparent holes that are NOT
     * connected to the border are preserved.
     */
    private void clearEdgeConnectedBackground(Pixmap pixmap) {
        int w = pixmap.getWidth();
        int h = pixmap.getHeight();
        boolean[][] visited = new boolean[h][w];
        ArrayDeque<int[]> queue = new ArrayDeque<>();

        for (int x = 0; x < w; x++) {
            enqueue(pixmap, visited, queue, x, 0);
            enqueue(pixmap, visited, queue, x, h - 1);
        }
        for (int y = 0; y < h; y++) {
            enqueue(pixmap, visited, queue, 0, y);
            enqueue(pixmap, visited, queue, w - 1, y);
        }

        while (!queue.isEmpty()) {
            int[] pt = queue.poll();
            int x = pt[0], y = pt[1];
            pixmap.drawPixel(x, y, 0);
            enqueue(pixmap, visited, queue, x + 1, y);
            enqueue(pixmap, visited, queue, x - 1, y);
            enqueue(pixmap, visited, queue, x, y + 1);
            enqueue(pixmap, visited, queue, x, y - 1);
        }
    }

    private void enqueue(Pixmap pixmap, boolean[][] visited,
                         ArrayDeque<int[]> queue, int x, int y) {
        if (x < 0 || y < 0 || x >= pixmap.getWidth() || y >= pixmap.getHeight()) return;
        if (visited[y][x]) return;
        visited[y][x] = true;
        if (isBackground(pixmap.getPixel(x, y))) {
            queue.addLast(new int[]{x, y});
        }
    }

    // ── Background pixel classifier ───────────────────────────────────────────

    /**
     * Returns true for pixel values that represent background rather than
     * sprite content.  Handles:
     * <ul>
     *   <li>Fully transparent (alpha = 0)</li>
     *   <li>Solid white/near-white (original spritesheet background)</li>
     *   <li>Mid-gray checkerboard squares (GIMP/Photoshop transparency markers)</li>
     *   <li>Semi-transparent near-white anti-aliasing fringe</li>
     * </ul>
     */
    private static boolean isBackground(int rgba8888) {
        int r = (rgba8888 >>> 24) & 0xff;
        int g = (rgba8888 >>> 16) & 0xff;
        int b = (rgba8888 >>>  8) & 0xff;
        int a =  rgba8888         & 0xff;
        int hi  = Math.max(r, Math.max(g, b));
        int lo  = Math.min(r, Math.min(g, b));
        int sat = hi - lo;

        if (a == 0) return true;
        // Solid near-white (white spritesheet background, even slightly tinted)
        if (lo >= 210 && sat <= 25 && a >= 200) return true;
        // Mid-gray checkerboard (GIMP/Photoshop: ~170-210 range, near-neutral)
        if (lo >= 150 && hi <= 220 && sat <= 25 && a >= 230) return true;
        // Semi-transparent near-white anti-aliasing fringe on bright background
        if (lo >= 190 && sat <= 30 && a < 210) return true;
        return false;
    }

    @Override
    public void dispose() {
        textures.values().forEach(Texture::dispose);
        textures.clear();
        regions.clear();
    }
}
