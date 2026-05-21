package it.unipd.daimyosimulator.gdx.ui;

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
    private final Label label;

    public PolicyPanel(Skin skin, GameAssetManager assetManager, CoreGameFacade facade,
                       Consumer<String> statusConsumer, Runnable refresh) {
        setBackground(skin.getDrawable("hud-panel"));
        label = new Label("", skin);
        defaults().pad(2);
        add(label).colspan(3).left();
        row();
        for (PolicyType type : PolicyType.values()) {
            TextButton button = new TextButton(shortName(type), skin);
            button.add(new Image(assetManager.getPolicyIcon(type))).size(24).padRight(2);
            button.addListener(new TextTooltip(tooltipFor(type), skin));
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    var result = facade.activatePolicy(type);
                    statusConsumer.accept(result.message());
                    refresh.run();
                }
            });
            add(button).width(118);
        }
    }

    public void refresh(PolicyViewModel policy) {
        label.setText("Policy " + policy.activeDisplayName()
                + "  Remaining " + policy.activeRemainingTicks()
                + "  Cooldowns " + policy.cooldowns());
    }

    private static String shortName(PolicyType type) {
        return switch (type) {
            case AGRICULTURAL_EXPANSION -> "Agriculture";
            case MILITARY_PROTECTION    -> "Military";
            case CRAFTSMEN_PRODUCTION   -> "Craft";
        };
    }

    private static String tooltipFor(PolicyType type) {
        return switch (type) {
            case AGRICULTURAL_EXPANSION ->
                "Agricultural Expansion – Duration: 5 ticks, Cooldown: 8 ticks\n"
                + "Rice production ×1.5 from paddies.\n"
                + "Rice Farmer tool consumption ×1.5.";
            case MILITARY_PROTECTION ->
                "Military Protection – Duration: 5 ticks, Cooldown: 8 ticks\n"
                + "Protection multiplier ×1.5 (reduces theft).\n"
                + "Samurai tool & luxury consumption ×1.5.";
            case CRAFTSMEN_PRODUCTION ->
                "Craftsmen Production – Duration: 5 ticks, Cooldown: 8 ticks\n"
                + "Timber, Tools, Luxury Goods production ×1.5.\n"
                + "Blacksmith & Artisan rice consumption ×1.5.";
        };
    }
}
