package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * In-game reference card listing all keyboard / mouse controls, grouped by category.
 * Opened by the "Cmd" button in the top bar of the HUD.
 */
public final class CommandsDialog extends Dialog {

    private static final Color COLOR_CATEGORY = new Color(0.98f, 0.82f, 0.35f, 1f);
    private static final Color COLOR_ACTION   = new Color(0.92f, 0.88f, 0.72f, 1f);
    private static final Color COLOR_KEY      = new Color(0.62f, 0.80f, 0.62f, 1f);
    private static final Color COLOR_DIVIDER  = new Color(0.40f, 0.35f, 0.25f, 1f);

    private static final Object[] SECTIONS = {
        // A String header starts a new category; String[2] is an action row.
        "Map Navigation",
        new String[]{ "Scroll / Pan map",   "Arrow keys  ·  WASD  ·  middle-drag" },
        new String[]{ "Zoom in / out",       "Scroll wheel" },
        "Building",
        new String[]{ "Place a building",    "Click building button  →  click grid tile" },
        new String[]{ "Cancel build mode",   "Right-click  or  Esc" },
        new String[]{ "Demolish building",   "Demolish button  →  click target tile" },
        new String[]{ "Inspect tile",        "Left-click tile  (outside build mode)" },
        new String[]{ "Open Market",         "Inspect Market tile  →  'Open Market'" },
        "Game Controls",
        new String[]{ "Advance one tick",    "NEXT button in speed panel" },
        new String[]{ "Pause / Resume",      "PAUSE button" },
        new String[]{ "Change speed",        "SPEED button  (cycles 1×  2×  4×)" },
        new String[]{ "Activate policy",     "Agriculture / Military / Craft button" },
        "Interface",
        new String[]{ "Save game",           "Save button  →  choose a slot  (1–5)" },
        new String[]{ "Load game",           "Load button  →  choose a slot  (1–5)" },
        new String[]{ "New game",            "New button  (resets village)" },
        new String[]{ "Help / Tutorial",     "? button  (top bar)" },
        new String[]{ "This dialog",         "Cmd button  (top bar)" },
        new String[]{ "Debug grid overlay",  "F3" },
    };

    public CommandsDialog(Skin skin) {
        super("Commands & Controls", skin);

        Table grid = new Table();
        grid.defaults().pad(3);
        grid.columnDefaults(0).width(200).left();
        grid.columnDefaults(1).width(310).left();

        for (Object entry : SECTIONS) {
            if (entry instanceof String header) {
                // Category header row
                addDivider(grid, skin, header);
            } else if (entry instanceof String[] row) {
                Label action = new Label(row[0], skin);
                action.setColor(COLOR_ACTION);
                Label key = new Label(row[1], skin, "dim");
                key.setColor(COLOR_KEY);
                grid.add(action).left().padLeft(12);
                grid.add(key).left();
                grid.row();
            }
        }

        ScrollPane scroll = new ScrollPane(grid, new ScrollPaneStyle());
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(true);
        getContentTable().pad(10).add(scroll).size(550, 420);

        button("Close");
        setMovable(true);
    }

    private static void addDivider(Table grid, Skin skin, String title) {
        Label cat = new Label("  " + title.toUpperCase(), skin, "title");
        cat.setColor(COLOR_CATEGORY);
        grid.add(cat).colspan(2).left().padTop(10).padBottom(2);
        grid.row();
        Label sep = new Label("──────────────────────────────────────────────────────────────", skin, "dim");
        sep.setColor(COLOR_DIVIDER);
        grid.add(sep).colspan(2).left().padBottom(2);
        grid.row();
    }
}
