package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.view.PolicyViewModel;
import it.unipd.daimyosimulator.core.policy.PolicyType;

import java.util.function.Consumer;

public final class PolicyPanel extends Table {
    private final Label label;

    public PolicyPanel(Skin skin, CoreGameFacade facade, Consumer<String> statusConsumer, Runnable refresh) {
        label = new Label("", skin);
        defaults().pad(2);
        add(label).colspan(3).left();
        row();
        for (PolicyType type : PolicyType.values()) {
            TextButton button = new TextButton(shortName(type), skin);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    var result = facade.activatePolicy(type);
                    statusConsumer.accept(result.message());
                    refresh.run();
                }
            });
            add(button).width(108);
        }
    }

    public void refresh(PolicyViewModel policy) {
        label.setText("Policy " + policy.activeDisplayName()
                + "  Remaining " + policy.activeRemainingTicks()
                + "  Cooldowns " + policy.cooldowns());
    }

    private String shortName(PolicyType type) {
        return switch (type) {
            case AGRICULTURAL_EXPANSION -> "Agriculture";
            case MILITARY_PROTECTION -> "Military";
            case CRAFTSMEN_PRODUCTION -> "Craft";
        };
    }
}
