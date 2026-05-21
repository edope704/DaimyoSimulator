package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import it.unipd.daimyosimulator.core.app.view.ResourceViewModel;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

import java.util.EnumMap;
import java.util.Map;

public final class ResourcePanel extends Table {
    private static final int LOW_THRESHOLD    = 30;
    private static final int DANGER_THRESHOLD = 10;

    private static final Color COLOR_NORMAL = Color.WHITE;
    private static final Color COLOR_LOW    = new Color(1f, 0.85f, 0.25f, 1f);
    private static final Color COLOR_DANGER = new Color(1f, 0.30f, 0.20f, 1f);

    private final Map<ResourceType, Label> amountLabels = new EnumMap<>(ResourceType.class);
    private final Map<ResourceType, Label> deltaLabels  = new EnumMap<>(ResourceType.class);

    public ResourcePanel(Skin skin, GameAssetManager assetManager) {
        setBackground(skin.getDrawable("hud-panel"));
        defaults().pad(2);
        for (ResourceType type : ResourceType.values()) {
            Image icon = new Image(assetManager.getResourceIcon(type));
            icon.addListener(new TextTooltip(iconTooltip(type), skin));
            add(icon).size(24);

            Label amountLabel = new Label("0", skin);
            amountLabels.put(type, amountLabel);
            add(amountLabel).width(46).left();

            // Delta label shows ±net per tick after each tick advance.
            Label deltaLabel = new Label("", skin, "dim");
            deltaLabels.put(type, deltaLabel);
            add(deltaLabel).width(36).left();
        }
    }

    public void refresh(ResourceViewModel resources) {
        updateAmounts(resources);
    }

    /** Refresh amounts and show per-tick net deltas (produced – consumed). */
    public void refreshWithDelta(ResourceViewModel resources, ResourceViewModel produced, ResourceViewModel consumed) {
        updateAmounts(resources);
        for (ResourceType type : ResourceType.values()) {
            int net = produced.amount(type) - consumed.amount(type);
            Label lbl = deltaLabels.get(type);
            if (net > 0) {
                lbl.setText("+" + net);
                lbl.setColor(new Color(0.4f, 1f, 0.4f, 1f));
            } else if (net < 0) {
                lbl.setText(String.valueOf(net));
                lbl.setColor(COLOR_DANGER);
            } else {
                lbl.setText("±0");
                lbl.setColor(new Color(0.6f, 0.6f, 0.6f, 1f));
            }
        }
    }

    private void updateAmounts(ResourceViewModel resources) {
        for (ResourceType type : ResourceType.values()) {
            int amount = resources.amount(type);
            Label lbl = amountLabels.get(type);
            lbl.setText(String.valueOf(amount));
            if (amount <= DANGER_THRESHOLD) {
                lbl.setColor(COLOR_DANGER);
            } else if (amount <= LOW_THRESHOLD) {
                lbl.setColor(COLOR_LOW);
            } else {
                lbl.setColor(COLOR_NORMAL);
            }
        }
    }

    private static String iconTooltip(ResourceType type) {
        return switch (type) {
            case RICE ->
                "Rice – Food supply.\n"
                + "Each villager consumes 2 rice/tick.\n"
                + "Produced by Rice Farmers on Farms adjacent to Paddies (+5/tick per paddy pair).\n"
                + "YELLOW = low  |  RED = starvation risk!";
            case TIMBER ->
                "Timber – Building material.\n"
                + "Spent when constructing buildings.\n"
                + "Produced by Woodcutters in Huts next to Forests (+3/tick per valid hut).";
            case TOOLS ->
                "Tools – Industrial resource.\n"
                + "Consumed by Rice Farmers and Samurai each tick.\n"
                + "Produced by Blacksmiths in Smithies (+2/tick, requires Mine).";
            case LUXURY_GOODS ->
                "Luxury Goods – Prestige resource.\n"
                + "Consumed by Samurai and Monks each tick.\n"
                + "Produced by Artisans in Workshops (+2 every 3 ticks, requires Mine).";
        };
    }
}
