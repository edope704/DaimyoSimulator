package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
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

        Label.LabelStyle warningStyle = new Label.LabelStyle(font, new Color(1f, 0.35f, 0.25f, 1f));
        skin.add("warning", warningStyle);

        Label.LabelStyle dimStyle = new Label.LabelStyle(font, new Color(0.6f, 0.6f, 0.6f, 1f));
        skin.add("dim", dimStyle);

        skin.add("hud-panel", solidDrawable(0.08f, 0.07f, 0.05f, 0.74f), Drawable.class);
        skin.add("hud-panel-light", solidDrawable(0.17f, 0.12f, 0.07f, 0.62f), Drawable.class);
        skin.add("hud-panel-red", solidDrawable(0.35f, 0.05f, 0.05f, 0.80f), Drawable.class);
        skin.add("hud-panel-selected", solidDrawable(0.20f, 0.18f, 0.06f, 0.85f), Drawable.class);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = new Color(0.98f, 0.90f, 0.72f, 1f);
        buttonStyle.downFontColor = Color.WHITE;
        buttonStyle.up = skin.getDrawable("hud-panel");
        buttonStyle.down = skin.getDrawable("hud-panel-light");
        buttonStyle.checked = skin.getDrawable("hud-panel-selected");
        buttonStyle.over = skin.getDrawable("hud-panel-light");
        skin.add("default", buttonStyle);

        // Red/destructive button style for demolish.
        TextButton.TextButtonStyle demolishStyle = new TextButton.TextButtonStyle();
        demolishStyle.font = font;
        demolishStyle.fontColor = new Color(1f, 0.55f, 0.45f, 1f);
        demolishStyle.downFontColor = new Color(1f, 0.3f, 0.2f, 1f);
        demolishStyle.up = skin.getDrawable("hud-panel");
        demolishStyle.down = skin.getDrawable("hud-panel-red");
        demolishStyle.checked = skin.getDrawable("hud-panel-red");
        demolishStyle.over = skin.getDrawable("hud-panel-light");
        skin.add("demolish", demolishStyle);

        // Tooltip style.
        Label.LabelStyle tooltipLabelStyle = new Label.LabelStyle(font, new Color(0.95f, 0.90f, 0.75f, 1f));
        skin.add("tooltip-font", tooltipLabelStyle);
        TextTooltip.TextTooltipStyle tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = tooltipLabelStyle;
        tooltipStyle.background = solidDrawable(0.05f, 0.04f, 0.03f, 0.92f);
        tooltipStyle.wrapWidth = 260;
        skin.add("default", tooltipStyle);

        // Window/dialog style (for Market and Tutorial dialogs).
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font;
        windowStyle.titleFontColor = new Color(0.98f, 0.85f, 0.55f, 1f);
        windowStyle.background = solidDrawable(0.10f, 0.08f, 0.05f, 0.95f);
        skin.add("default", windowStyle);

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
