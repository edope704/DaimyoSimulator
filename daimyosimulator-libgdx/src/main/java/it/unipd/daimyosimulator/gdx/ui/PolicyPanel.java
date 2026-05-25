package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.view.PolicyViewModel;
import it.unipd.daimyosimulator.core.policy.PolicyType;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

import java.util.function.Consumer;

public final class PolicyPanel extends Table {
    private static final Color COLOR_ACTIVE = new Color(0.98f, 0.82f, 0.35f, 1f);
    private static final Color COLOR_IDLE   = new Color(0.75f, 0.70f, 0.55f, 1f);

    private final Label statusLabel;

    public PolicyPanel(Skin skin, GameAssetManager assetManager, CoreGameFacade facade,
                       Consumer<String> statusConsumer, Runnable refresh) {
        setBackground(skin.getDrawable("hud-panel"));
        pad(4);
        defaults().pad(2);

        Label header = new Label("POLICIES", skin, "title");
        add(header).left().padBottom(4);
        row();

        statusLabel = new Label("None active", skin, "dim");
        statusLabel.setColor(new Color(0.60f, 0.60f, 0.50f, 1f));
        add(statusLabel).left().padBottom(4);
        row();

        for (PolicyType type : PolicyType.values()) {
            Table cell = new Table();
            cell.add(new Image(assetManager.getPolicyIcon(type))).size(22).padRight(3);
            TextButton button = new TextButton(shortName(type), skin);
            button.addListener(new TextTooltip(tooltipFor(type), skin));
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    var result = facade.activatePolicy(type);
                    statusConsumer.accept(result.message());
                    refresh.run();
                }
            });
            cell.add(button);
            add(cell).fillX();
            row();
        }
    }

    public void refresh(PolicyViewModel policy) {
        if (policy.activePolicy() != null && policy.activeRemainingTicks() > 0) {
            statusLabel.setText(policy.activeDisplayName() + "  (" + policy.activeRemainingTicks() + " left)");
            statusLabel.setColor(COLOR_ACTIVE);
        } else {
            statusLabel.setText("None active");
            statusLabel.setColor(new Color(0.60f, 0.60f, 0.50f, 1f));
        }
    }

    private static String shortName(PolicyType type) {
        return switch (type) {
            case AGRICULTURAL_EXPANSION -> "Agriculture";
            case MILITARY_PROTECTION    -> "Military";
            case CRAFTSMEN_PRODUCTION   -> "Craftsmen";
        };
    }

    private static String tooltipFor(PolicyType type) {
        return switch (type) {
            case AGRICULTURAL_EXPANSION ->
                "Agricultural Expansion – 5 ticks  ·  Cooldown: 8 ticks\n"
                + "Rice Paddy output ×1.5.\n"
                + "Rice Farmer tool consumption ×1.5.";
            case MILITARY_PROTECTION ->
                "Military Protection – 5 ticks  ·  Cooldown: 8 ticks\n"
                + "Protection multiplier ×1.5 (reduces theft events).\n"
                + "Samurai tool & luxury consumption ×1.5.";
            case CRAFTSMEN_PRODUCTION ->
                "Craftsmen Production – 5 ticks  ·  Cooldown: 8 ticks\n"
                + "Timber, Tools, Luxury Goods output ×1.5.\n"
                + "Blacksmith & Artisan rice consumption ×1.5.";
        };
    }
}
