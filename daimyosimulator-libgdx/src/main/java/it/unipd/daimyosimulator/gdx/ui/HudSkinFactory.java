package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

public final class HudSkinFactory {
    public Skin create(GameAssetManager assetManager) {
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = new TextureRegionDrawable(assetManager.getRegion("ui_button"));
        buttonStyle.down = new TextureRegionDrawable(assetManager.getRegion("tile_dirt"));
        buttonStyle.checked = new TextureRegionDrawable(assetManager.getRegion("tile_grass"));
        skin.add("default", buttonStyle);
        return skin;
    }
}
