package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
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

        content.add(new Image(skin.getDrawable("icon-alert"))).size(32).center().padBottom(8);
        content.row();

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

    /** Hide and remove all EventModal instances currently on the stage. */
    public static void dismissAll(Stage stage) {
        if (stage == null) return;
        Array<Actor> actors = new Array<>(stage.getActors());
        for (Actor a : actors) {
            if (a instanceof EventModal modal) {
                modal.hide();
            }
        }
    }

    /** Show a simple alert dialog with a title and message. */
    public static void showAlert(Skin skin, String title, String message, Stage stage) {
        if (stage == null) return;
        Dialog alert = new Dialog(title, skin);
        Table content = alert.getContentTable();
        content.pad(14);
        content.add(new Image(skin.getDrawable("icon-alert"))).size(28).center().padBottom(6);
        content.row();
        Label msg = new Label(message, skin, "warning");
        msg.setWrap(true);
        content.add(msg).width(380).left();
        alert.button("OK");
        alert.setMovable(true);
        alert.show(stage);
    }
}
