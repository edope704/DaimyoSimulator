package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import it.unipd.daimyosimulator.core.event.EventReport;

import java.util.List;

/**
 * Modal pop-up shown whenever one or more random events fire in a tick.
 * Each event entry shows its name prominently, its trigger explanation,
 * and the concrete consequence that occurred.
 */
public final class EventModal extends Dialog {

    public EventModal(Skin skin, List<EventReport> reports) {
        super("! Random Event !", skin);
        Table content = getContentTable();
        content.pad(14);

        for (int i = 0; i < reports.size(); i++) {
            EventReport r = reports.get(i);

            // Event name — highlighted in warning colour.
            Label nameLabel = new Label(r.name(), skin, "warning");
            content.add(nameLabel).left().padTop(i == 0 ? 0 : 12);
            content.row();

            // Explanation (why it happened).
            Label explainLabel = new Label(r.explanation(), skin, "dim");
            explainLabel.setWrap(true);
            content.add(explainLabel).width(460).left().padTop(2);
            content.row();

            // Consequence (what actually changed).
            Label consequenceLabel = new Label(r.consequence(), skin);
            consequenceLabel.setWrap(true);
            content.add(consequenceLabel).width(460).left().padTop(2);
            content.row();
        }

        button("Dismiss");
        setMovable(true);
    }

    /** Show centred on stage and focus. */
    public static void showIfAny(Skin skin, List<EventReport> reports, Stage stage) {
        if (reports.isEmpty() || stage == null) return;
        new EventModal(skin, reports).show(stage);
    }
}
