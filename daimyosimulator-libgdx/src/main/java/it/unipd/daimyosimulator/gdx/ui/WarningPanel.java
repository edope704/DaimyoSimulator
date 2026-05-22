package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import it.unipd.daimyosimulator.core.app.view.ResourceViewModel;
import it.unipd.daimyosimulator.core.resource.ResourceType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Right-side bulletin board. Tracks the last HISTORY_SIZE resource snapshots
 * and shows a warning row whenever a resource has been decreasing for
 * TREND_TICKS consecutive ticks.  Clicking the warning label reveals an
 * actionable tooltip.
 */
public final class WarningPanel extends Table {
    private static final int HISTORY_SIZE  = 4;
    private static final int TREND_TICKS   = 3;

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
        warningRows.defaults().left().pad(1);
        add(warningRows).left();
    }

    /** Call on every tick with the latest resource snapshot. */
    public void onTick(ResourceViewModel resources) {
        history.addLast(resources);
        if (history.size() > HISTORY_SIZE) {
            history.removeFirst();
        }
        rebuildWarnings();
    }

    private void rebuildWarnings() {
        warningRows.clearChildren();

        if (history.size() < TREND_TICKS) {
            Label waiting = new Label("(monitoring…)", skin, "dim");
            warningRows.add(waiting).left();
            return;
        }

        List<ResourceViewModel> snapshots = new ArrayList<>(history);
        boolean anyWarning = false;

        for (ResourceType type : ResourceType.values()) {
            if (isTrendingDown(snapshots, type)) {
                anyWarning = true;
                Label warn = new Label("▼ " + displayName(type) + " falling!", skin, "warning");
                warn.addListener(new TextTooltip(adviceFor(type), skin));
                warningRows.add(warn).left();
                warningRows.row();
            }
        }

        if (!anyWarning) {
            Label ok = new Label("All resources stable", skin, "dim");
            warningRows.add(ok).left();
        }
    }

    private boolean isTrendingDown(List<ResourceViewModel> snapshots, ResourceType type) {
        // Check that the last TREND_TICKS snapshots are strictly monotone-decreasing.
        for (int i = snapshots.size() - TREND_TICKS; i < snapshots.size() - 1; i++) {
            if (snapshots.get(i).amount(type) <= snapshots.get(i + 1).amount(type)) {
                return false;
            }
        }
        return true;
    }

    private static String displayName(ResourceType type) {
        return switch (type) {
            case RICE         -> "Rice";
            case TIMBER       -> "Timber";
            case TOOLS        -> "Tools";
            case LUXURY_GOODS -> "Luxury";
        };
    }

    private static String adviceFor(ResourceType type) {
        return switch (type) {
            case RICE ->
                "Rice is depleting!\n"
                + "• Build more Rice Farms (18 timber)\n"
                + "• Place Rice Paddies next to Farms (+5 rice/tick)\n"
                + "• Activate Agricultural Expansion policy\n"
                + "• Reduce population growth until stable";
            case TIMBER ->
                "Timber is depleting!\n"
                + "• Build Woodcutter's Huts adjacent to Forests (20 timber)\n"
                + "• Forests on the map border are always available\n"
                + "• Avoid building until stock recovers";
            case TOOLS ->
                "Tools are depleting!\n"
                + "• Build a Mine (25 timber) if none exists\n"
                + "• Build a Smithy next to the Mine (30 timber)\n"
                + "• Activate Craftsmen Production policy\n"
                + "• Fewer Samurai reduces tool consumption";
            case LUXURY_GOODS ->
                "Luxury Goods are depleting!\n"
                + "• Build a Workshop (35 timber, requires Mine)\n"
                + "• Artisans produce +2 luxury every 3 ticks\n"
                + "• Fewer Monks reduces luxury consumption";
        };
    }
}
