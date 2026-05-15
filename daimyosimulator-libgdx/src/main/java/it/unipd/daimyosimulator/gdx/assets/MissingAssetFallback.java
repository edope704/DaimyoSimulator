package it.unipd.daimyosimulator.gdx.assets;

public final class MissingAssetFallback {
    public static final String NAME = "missing_asset";
    public static final String PATH = "assets/textures/placeholders/missing_asset.png";

    public String fallbackName() {
        return NAME;
    }
}
