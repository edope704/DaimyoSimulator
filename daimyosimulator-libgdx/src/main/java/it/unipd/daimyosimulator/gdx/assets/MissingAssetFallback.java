package it.unipd.daimyosimulator.gdx.assets;

import com.badlogic.gdx.graphics.Color;

public final class MissingAssetFallback {
    public static final String NAME = "missing_asset";
    public static final Color COLOR = Color.MAGENTA;

    public String fallbackName() {
        return NAME;
    }
}
