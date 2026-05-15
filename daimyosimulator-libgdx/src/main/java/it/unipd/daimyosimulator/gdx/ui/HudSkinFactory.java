package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

public final class HudSkinFactory {
    public Skin create(GameAssetManager assetManager) {
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", labelStyle);
        skin.add("hud-panel", solidDrawable(0.08f, 0.07f, 0.05f, 0.74f), Drawable.class);
        skin.add("hud-panel-light", solidDrawable(0.17f, 0.12f, 0.07f, 0.62f), Drawable.class);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = new Color(0.98f, 0.90f, 0.72f, 1f);
        buttonStyle.downFontColor = Color.WHITE;
        buttonStyle.up = skin.getDrawable("hud-panel");
        buttonStyle.down = skin.getDrawable("hud-panel-light");
        buttonStyle.checked = skin.getDrawable("hud-panel-light");
        skin.add("default", buttonStyle);
        return skin;
    }

    private Drawable solidDrawable(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
