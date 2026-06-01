package gdx.render;

public final class RenderConstants {
    public static final int TILE_SIZE = 64;
    /** Total rendered grid side length (includes playable area + forest border on each side). */
    public static final int RENDER_GRID_SIZE = 30;
    /** Number of forest-only tiles on each side of the playable area. */
    public static final int PLAYABLE_OFFSET = 5;

    private RenderConstants() {
    }
}
