package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.graphics.Color;
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
 *
 * Negative events (resource losses) are rendered in red with the alert icon visible.
 * Positive events (resource gains) are rendered in green with the alert icon hidden.
 */
public final class EventModal extends Dialog {

    private static final Color COLOR_GOOD = new Color(0.25f, 1.00f, 0.35f, 1f);
    private static final Color COLOR_BAD  = new Color(1.00f, 0.25f, 0.10f, 1f);

    public EventModal(Skin skin, List<EventReport> reports) {
        super(hasAnyNegative(reports) ? "! Random Event !" : "Good News!", skin);
        Table content = getContentTable();
        content.pad(14);

        // Alert icon only shown when at least one negative event is in the batch.
        if (hasAnyNegative(reports)) {
            content.add(new Image(skin.getDrawable("icon-alert"))).size(32).center().padBottom(8);
            content.row();
        }

        for (int i = 0; i < reports.size(); i++) {
            EventReport r = reports.get(i);
            Color textColor = r.positive() ? COLOR_GOOD : COLOR_BAD;

            // Event name
            Label nameLabel = new Label(r.name(), skin, "warning");
            nameLabel.setColor(textColor);
            content.add(nameLabel).left().padTop(i == 0 ? 0 : 12);
            content.row();

            // Explanation (trigger context — always grey)
            Label explainLabel = new Label(r.explanation(), skin, "dim");
            explainLabel.setWrap(true);
            content.add(explainLabel).width(460).left().padTop(2);
            content.row();

            // Consequence (resource change — coloured by polarity)
            Label consequenceLabel = new Label(r.consequence(), skin);
            consequenceLabel.setColor(textColor);
            consequenceLabel.setWrap(true);
            content.add(consequenceLabel).width(460).left().padTop(2);
            content.row();
        }

        button("Dismiss");
        setMovable(true);
    }

    private static boolean hasAnyNegative(List<EventReport> reports) {
        return reports.stream().anyMatch(r -> !r.positive());
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

    /** Show an informational alert (no icon, neutral styling). */
    public static void showInfo(Skin skin, String title, String message, Stage stage) {
        if (stage == null) return;
        Dialog info = new Dialog(title, skin);
        Table content = info.getContentTable();
        content.pad(14);
        Label msg = new Label(message, skin);
        msg.setColor(new Color(0.90f, 0.85f, 0.70f, 1f));
        msg.setWrap(true);
        content.add(msg).width(400).left();
        info.button("OK");
        info.setMovable(true);
        info.show(stage);
    }
}
