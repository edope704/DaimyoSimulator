package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.app.view.ResourceViewModel;
import it.unipd.daimyosimulator.core.resource.ResourceType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Right-side bulletin board. Tracks the last HISTORY_SIZE resource snapshots
 * and shows a clickable warning card whenever a resource has been decreasing
 * for TREND_TICKS consecutive ticks.  Clicking a card opens a modal with
 * exact, actionable suggestions to fix the shortage.
 */
public final class WarningPanel extends Table {
    private static final int HISTORY_SIZE = 4;
    private static final int TREND_TICKS  = 3;

    private static final Color COLOR_WARN = new Color(1f, 0.65f, 0.20f, 1f);
    private static final Color COLOR_OK   = new Color(0.50f, 0.80f, 0.50f, 1f);

    private final Skin skin;
    private final Deque<ResourceViewModel> history = new ArrayDeque<>(HISTORY_SIZE + 1);
    private final Table warningRows;

    public WarningPanel(Skin skin) {
        this.skin = skin;
        setBackground(skin.getDrawable("hud-panel"));
        defaults().left().pad(3);

        Label header = new Label("WARNINGS", skin, "title");
        add(header).left().padBottom(4);
        row();

        warningRows = new Table();
        warningRows.defaults().left().pad(2);
        add(warningRows).left();
    }

    /** Call on every tick with the latest resource snapshot. */
    public void onTick(ResourceViewModel resources) {
        history.addLast(resources);
        if (history.size() > HISTORY_SIZE) history.removeFirst();
        rebuildWarnings();
    }

    private void rebuildWarnings() {
        warningRows.clearChildren();

        if (history.size() < TREND_TICKS) {
            Label waiting = new Label("(monitoring…)", skin, "dim");
            waiting.setColor(new Color(0.6f, 0.6f, 0.6f, 1f));
            warningRows.add(waiting).left();
            return;
        }

        List<ResourceViewModel> snapshots = new ArrayList<>(history);
        boolean anyWarning = false;

        for (ResourceType type : ResourceType.values()) {
            if (isTrendingDown(snapshots, type)) {
                anyWarning = true;
                TextButton card = new TextButton("v " + displayName(type) + " falling!", skin);
                card.getLabel().setColor(COLOR_WARN);
                card.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                        showAdviceDialog(type);
                    }
                });
                warningRows.add(card).fillX().padBottom(2);
                warningRows.row();
            }
        }

        if (!anyWarning) {
            Label ok = new Label("+ All stable", skin, "dim");
            ok.setColor(COLOR_OK);
            warningRows.add(ok).left();
        }
    }

    private void showAdviceDialog(ResourceType type) {
        if (getStage() == null) return;

        Dialog dialog = new Dialog("Resource Warning: " + displayName(type), skin) {
            {
                setMovable(true);
                button("Close");
            }
        };

        Table content = dialog.getContentTable();
        content.pad(16);

        Label title = new Label("v  " + displayName(type) + " is depleting!", skin, "title");
        title.setColor(COLOR_WARN);
        content.add(title).left().padBottom(12);
        content.row();

        Label advice = new Label(adviceFor(type), skin, "dim");
        advice.setWrap(true);
        advice.setColor(new Color(0.85f, 0.80f, 0.65f, 1f));
        content.add(advice).width(340).left();

        dialog.show(getStage());
    }

    private boolean isTrendingDown(List<ResourceViewModel> snapshots, ResourceType type) {
        for (int i = snapshots.size() - TREND_TICKS; i < snapshots.size() - 1; i++) {
            if (snapshots.get(i).amount(type) <= snapshots.get(i + 1).amount(type)) return false;
        }
        return true;
    }

    private static String displayName(ResourceType type) {
        return switch (type) {
            case RICE         -> "Rice";
            case TIMBER       -> "Timber";
            case TOOLS        -> "Tools";
            case LUXURY_GOODS -> "Luxury Goods";
        };
    }

    private static String adviceFor(ResourceType type) {
        return switch (type) {
            case RICE ->
                "To increase rice production:\n"
                + "  • Build more Rice Farms (cost: 18 timber)\n"
                + "  • Place Rice Paddies adjacent to Farms (+5 rice/tick each)\n"
                + "  • Activate Agricultural Expansion policy (×1.5 paddy output)\n"
                + "  • Reduce population growth until stocks stabilise";
            case TIMBER ->
                "To increase timber production:\n"
                + "  • Build Woodcutter's Huts near Forest tiles (cost: 20 timber)\n"
                + "  • Forest border tiles are always available — expand outward\n"
                + "  • Pause construction projects until stocks recover";
            case TOOLS ->
                "To increase tool production:\n"
                + "  • Build a Mine first if none exists (cost: 25 timber)\n"
                + "  • Build a Smithy adjacent to the Mine (cost: 30 timber)\n"
                + "  • Activate Craftsmen Production policy (×1.5 output)\n"
                + "  • Reduce Samurai count to lower tool consumption";
            case LUXURY_GOODS ->
                "To increase luxury goods:\n"
                + "  • Build a Workshop (cost: 35 timber; requires a Mine)\n"
                + "  • Artisans produce +2 luxury goods every 3 ticks\n"
                + "  • Activate Craftsmen Production policy (×1.5 output)\n"
                + "  • Reduce Monk count to lower luxury consumption";
        };
    }
}
