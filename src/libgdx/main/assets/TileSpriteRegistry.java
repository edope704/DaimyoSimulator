package gdx.assets;

public final class TileSpriteRegistry {
    public String spriteName(TileType type) {
        return switch (type) {
            case GRASS -> "tile_grass";
            case DIRT -> "tile_dirt";
        };
    }
}
