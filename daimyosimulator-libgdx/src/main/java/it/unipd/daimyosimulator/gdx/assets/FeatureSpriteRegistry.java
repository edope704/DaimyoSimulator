package it.unipd.daimyosimulator.gdx.assets;

import it.unipd.daimyosimulator.core.domain.NaturalFeature;

public final class FeatureSpriteRegistry {
    public String spriteName(NaturalFeature feature) {
        return feature == NaturalFeature.FOREST ? "feature_forest" : MissingAssetFallback.NAME;
    }
}
