package gdx.assets;

import core.domain.NaturalFeature;

public final class FeatureSpriteRegistry {
    public String spriteName(NaturalFeature feature) {
        return feature == NaturalFeature.FOREST ? "feature_forest" : MissingAssetFallback.NAME;
    }
}
