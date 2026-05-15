package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import it.unipd.daimyosimulator.core.app.view.EventLogViewModel;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.assets.ParameterType;

import java.util.ArrayList;
import java.util.List;

public final class EventLogPanel extends Table {
    private final Label label;
    private final List<String> events = new ArrayList<>();

    public EventLogPanel(Skin skin, GameAssetManager assetManager) {
        setBackground(skin.getDrawable("hud-panel"));
        add(new Image(assetManager.getParameterIcon(ParameterType.EVENT_ALERT))).size(28).left().top();
        label = new Label("", skin);
        label.setWrap(true);
        add(label).left().width(360);
    }

    public void refresh(EventLogViewModel eventLog) {
        events.clear();
        events.addAll(eventLog.events());
        redraw();
    }

    public void addStatus(String status) {
        if (status != null && !status.isBlank()) {
            events.add(status);
            while (events.size() > 8) {
                events.remove(0);
            }
            redraw();
        }
    }

    private void redraw() {
        StringBuilder builder = new StringBuilder();
        for (String event : events) {
            builder.append(event).append('\n');
        }
        label.setText(builder.toString());
    }
}
