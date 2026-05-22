package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

import java.util.ArrayList;
import java.util.List;

public final class HudSkinFactory {
    // All textures created here; kept alive with the skin.
    private final List<Texture> ownedTextures = new ArrayList<>();

    public Skin create(GameAssetManager assetManager) {
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.1f);
        skin.add("default-font", font);

        // ── Label styles ──────────────────────────────────────────────────────
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", labelStyle);

        Label.LabelStyle warningStyle = new Label.LabelStyle(font, new Color(1f, 0.35f, 0.25f, 1f));
        skin.add("warning", warningStyle);

        Label.LabelStyle dimStyle = new Label.LabelStyle(font, new Color(0.6f, 0.6f, 0.6f, 1f));
        skin.add("dim", dimStyle);

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, new Color(0.98f, 0.82f, 0.35f, 1f));
        skin.add("title", titleStyle);

        // ── Panel drawables (rounded) ─────────────────────────────────────────
        int r = 5; // corner radius for panels
        skin.add("hud-panel",          roundedBorderedDrawable(0.08f, 0.07f, 0.05f, 0.80f,
                                                                 0.25f, 0.18f, 0.08f, 0.90f, r), Drawable.class);
        skin.add("hud-panel-light",    roundedBorderedDrawable(0.17f, 0.12f, 0.07f, 0.72f,
                                                                 0.35f, 0.24f, 0.10f, 0.85f, r), Drawable.class);
        skin.add("hud-panel-red",      roundedBorderedDrawable(0.35f, 0.05f, 0.05f, 0.88f,
                                                                 0.60f, 0.12f, 0.08f, 0.95f, r), Drawable.class);
        skin.add("hud-panel-selected", roundedBorderedDrawable(0.20f, 0.18f, 0.06f, 0.90f,
                                                                 0.55f, 0.48f, 0.10f, 0.95f, r), Drawable.class);
        skin.add("hud-panel-warning",  roundedBorderedDrawable(0.25f, 0.10f, 0.02f, 0.90f,
                                                                 0.70f, 0.35f, 0.05f, 0.95f, r), Drawable.class);

        // ── Default button style ───────────────────────────────────────────────
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = new Color(0.98f, 0.90f, 0.72f, 1f);
        buttonStyle.downFontColor = Color.WHITE;
        buttonStyle.up = skin.getDrawable("hud-panel");
        buttonStyle.down = skin.getDrawable("hud-panel-light");
        buttonStyle.checked = skin.getDrawable("hud-panel-selected");
        buttonStyle.over = skin.getDrawable("hud-panel-light");
        skin.add("default", buttonStyle);

        // ── Demolish button ────────────────────────────────────────────────────
        TextButton.TextButtonStyle demolishStyle = new TextButton.TextButtonStyle();
        demolishStyle.font = font;
        demolishStyle.fontColor = new Color(1f, 0.55f, 0.45f, 1f);
        demolishStyle.downFontColor = new Color(1f, 0.3f, 0.2f, 1f);
        demolishStyle.up = skin.getDrawable("hud-panel");
        demolishStyle.down = skin.getDrawable("hud-panel-red");
        demolishStyle.checked = skin.getDrawable("hud-panel-red");
        demolishStyle.over = skin.getDrawable("hud-panel-light");
        skin.add("demolish", demolishStyle);

        // ── Tooltip style ──────────────────────────────────────────────────────
        Label.LabelStyle tooltipLabelStyle = new Label.LabelStyle(font, new Color(0.95f, 0.90f, 0.75f, 1f));
        skin.add("tooltip-font", tooltipLabelStyle);
        TextTooltip.TextTooltipStyle tooltipStyle = new TextTooltip.TextTooltipStyle();
        tooltipStyle.label = tooltipLabelStyle;
        tooltipStyle.background = roundedBorderedDrawable(0.06f, 0.05f, 0.03f, 0.95f,
                0.30f, 0.22f, 0.10f, 1.0f, r);
        tooltipStyle.wrapWidth = 280;
        skin.add("default", tooltipStyle);

        // ── Window/dialog style ────────────────────────────────────────────────
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font;
        windowStyle.titleFontColor = new Color(0.98f, 0.85f, 0.45f, 1f);
        // Raised panel: dark fill with a warm border.
        windowStyle.background = roundedBorderedDrawable(0.10f, 0.08f, 0.05f, 0.97f,
                0.38f, 0.28f, 0.12f, 1.0f, r);
        skin.add("default", windowStyle);

        return skin;
    }

    /**
     * Creates a NinePatch drawable with rounded corners.
     * Outer 1px ring is drawn in the border colour; inner fill is the panel colour.
     */
    private Drawable roundedBorderedDrawable(
            float fr, float fg, float fb, float fa,
            float br, float bg, float bb, float ba,
            int radius) {

        int size = Math.max(radius * 2 + 6, 16);
        Pixmap pm = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pm.setColor(0, 0, 0, 0);
        pm.fill();

        // Draw border layer.
        drawRoundedRect(pm, 0, 0, size, size, radius,
                new Color(br, bg, bb, ba));

        // Draw fill layer inset by 1px.
        if (radius > 1) {
            drawRoundedRect(pm, 1, 1, size - 2, size - 2, radius - 1,
                    new Color(fr, fg, fb, fa));
        } else {
            pm.setColor(fr, fg, fb, fa);
            pm.fillRectangle(1, 1, size - 2, size - 2);
        }

        Texture tex = new Texture(pm);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pm.dispose();
        ownedTextures.add(tex);

        // Split: corners are radius×radius; center stretches.
        NinePatch np = new NinePatch(tex, radius, radius, radius, radius);
        return new NinePatchDrawable(np);
    }

    private void drawRoundedRect(Pixmap pm, int x, int y, int w, int h, int r, Color c) {
        pm.setColor(c);
        // Horizontal fill strips.
        pm.fillRectangle(x + r, y, w - 2 * r, h);
        // Vertical fill strips.
        pm.fillRectangle(x, y + r, w, h - 2 * r);
        // Four corner circles.
        pm.fillCircle(x + r, y + r, r);
        pm.fillCircle(x + w - r - 1, y + r, r);
        pm.fillCircle(x + r, y + h - r - 1, r);
        pm.fillCircle(x + w - r - 1, y + h - r - 1, r);
    }

    /** Flat 2×2 drawable (kept for overlay uses that don't need rounded corners). */
    private Drawable solidDrawable(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        ownedTextures.add(texture);
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
