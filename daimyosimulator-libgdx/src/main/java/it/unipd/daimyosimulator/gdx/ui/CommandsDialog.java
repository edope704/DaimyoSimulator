package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;

/**
 * In-game reference card listing all keyboard / mouse commands.
 * Opened by the "Cmd" button in the top bar of the HUD.
 */
public final class CommandsDialog extends Dialog {

    private static final String[][] COMMANDS = {
        // { "Action", "Input" }
        { "Place building",         "Click building button  →  click grid tile" },
        { "Cancel build mode",      "Right-click  or  Escape" },
        { "Inspect cell",           "Left-click any tile (outside build mode)" },
        { "Open Market",            "Inspect a Market tile  →  'Open Market' button" },
        { "Demolish building",      "Demolish button  →  click target building" },
        { "Advance one tick",       "NEXT button in speed panel" },
        { "Pause / Resume",         "PAUSE button" },
        { "Change speed",           "SPEED button  (cycles 1×  2×  4×)" },
        { "Scroll map",             "Arrow keys  or  WASD  or  middle-click drag" },
        { "Zoom map",               "Scroll wheel" },
        { "Debug grid overlay",     "F3" },
        { "Open Help / Tutorial",   "? button (top-right)" },
        { "Open this dialog",       "Cmd button (top-right)" },
        { "Save game",              "Save button  →  choose slot" },
        { "Load game",              "Load button  →  choose slot" },
        { "New game",               "New button  (resets everything)" },
        { "Activate policy",        "Click Agriculture / Military / Craft button" },
    };

    public CommandsDialog(Skin skin) {
        super("Commands Reference", skin);

        Table grid = new Table();
        grid.defaults().pad(3);
        grid.columnDefaults(0).width(210).left();
        grid.columnDefaults(1).width(300).left();

        // Header.
        grid.add(new Label("Action", skin, "title")).left();
        grid.add(new Label("How to trigger", skin, "title")).left();
        grid.row().padBottom(2);
        grid.add(new Label("─────────────────────", skin, "dim")).left();
        grid.add(new Label("────────────────────────────────────────────", skin, "dim")).left();
        grid.row();

        for (String[] row : COMMANDS) {
            grid.add(new Label(row[0], skin)).left();
            grid.add(new Label(row[1], skin, "dim")).left();
            grid.row();
        }

        ScrollPane scroll = new ScrollPane(grid, new ScrollPaneStyle());
        scroll.setScrollingDisabled(true, false);
        getContentTable().add(scroll).size(540, 380).pad(10);

        button("Close");
        setMovable(true);
    }
}
