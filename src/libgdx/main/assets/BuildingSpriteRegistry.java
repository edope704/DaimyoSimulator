package gdx.assets;

import core.building.BuildingType;

import java.util.EnumMap;
import java.util.Map;

public final class BuildingSpriteRegistry {
    private final Map<BuildingType, String> sprites = new EnumMap<>(BuildingType.class);

    public BuildingSpriteRegistry() {
        sprites.put(BuildingType.DWELLING, "building_dwelling");
        sprites.put(BuildingType.RICE_FARM, "building_rice_farm");
        sprites.put(BuildingType.RICE_PADDY, "building_rice_paddy");
        sprites.put(BuildingType.WOODCUTTERS_HUT, "building_woodcutters_hut");
        sprites.put(BuildingType.MINE, "building_mine");
        sprites.put(BuildingType.SMITHY, "building_smithy");
        sprites.put(BuildingType.WORKSHOP, "building_workshop");
        sprites.put(BuildingType.MARKET, "building_market");
        sprites.put(BuildingType.GUARD_POST, "building_guard_post");
        sprites.put(BuildingType.TEMPLE, "building_temple");
    }

    public String spriteName(BuildingType type) {
        return sprites.getOrDefault(type, MissingAssetFallback.NAME);
    }
}
