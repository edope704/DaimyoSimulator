package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Structured help modal.  Uses colour-coded labels for visual hierarchy:
 *   title   (gold)   = section headers
 *   warning (red)    = critical rules the player must know
 *   hint    (amber)  = keys / building names
 *   dim     (grey)   = supplementary / optional detail
 *   default (white)  = regular content
 *
 * All strings are plain ASCII to avoid glyph-missing squares with the default BitmapFont.
 */
public final class TutorialDialog extends Dialog {

    private static final int WRAP_WIDTH = 500;

    public TutorialDialog(Skin skin) {
        super("Help & Tutorial", skin);

        Table content = new Table();
        content.top().left();
        content.defaults().left();

        h1(content, skin, "DAIMYO SIMULATOR - QUICK GUIDE");
        sep(content, skin);

        // ── GOAL ─────────────────────────────────────────────────────────────
        h2(content, skin, "GOAL");
        body(content, skin,
            "Grow your village, feed your people, and survive random events.\n"
            + "A new game starts with 1 Woodcutter and 2 Dwellings already placed.");

        // ── CONTROLS ─────────────────────────────────────────────────────────
        h2(content, skin, "CONTROLS");
        kv(content, skin, "Left-click build btn",  "enter build mode");
        kv(content, skin, "Left-click grid tile",  "place building / inspect cell");
        kv(content, skin, "Right-click / Esc",     "cancel build or demolish mode");
        kv(content, skin, "Next button",           "advance one tick manually");
        kv(content, skin, "Pause button",          "toggle auto-advance");
        kv(content, skin, "Speed button",          "cycle 1x  2x  4x speed");
        kv(content, skin, "Demolish button",       "remove a building (no refund)");
        kv(content, skin, "F3",                    "toggle debug grid overlay");

        // ── RESOURCES ────────────────────────────────────────────────────────
        h2(content, skin, "RESOURCES");
        warn(content, skin, "YELLOW number = low stock  |  RED = critical / danger!");
        kv(content, skin, "Rice",         "consumed 2/tick per villager. Runs out -> starvation.");
        kv(content, skin, "Timber",       "spent to construct buildings.");
        kv(content, skin, "Tools",        "consumed by Farmers & Samurai each tick.");
        kv(content, skin, "Luxury Goods", "consumed by Samurai & Monks each tick.");

        // ── BUILD LIMIT ───────────────────────────────────────────────────────
        h2(content, skin, "BUILD LIMIT");
        warn(content, skin, "You may only place 2 buildings per tick.");
        dim(content, skin,
            "The counter 'Builds: x/y' in the left panel resets each tick.\n"
            + "Exceeding the limit triggers an alert pop-up.");

        // ── BUILDINGS ─────────────────────────────────────────────────────────
        h2(content, skin, "BUILDINGS  (cost in timber)");
        kv(content, skin, "Dwelling   15t", "houses 4 villagers. Unhoused cannot work.");
        kv(content, skin, "Farm       18t", "3 Farmer slots. Needs adjacent Paddies.");
        kv(content, skin, "Paddy       8t", "boosts adjacent Farm. +5 rice/tick per pair.");
        kv(content, skin, "Woodcutter 20t", "3 Woodcutter slots. MUST touch a Forest.");
        kv(content, skin, "Mine       25t", "prerequisite for Smithy & Workshop.");
        kv(content, skin, "Smithy     30t", "2 Blacksmith slots -> +2 Tools/tick.");
        kv(content, skin, "Workshop   35t", "2 Artisan slots -> +2 Luxury/3 ticks.");
        kv(content, skin, "Market     25t", "2 Trader slots. Enables resource trading.");
        kv(content, skin, "Guard Post 25t", "2 Samurai slots -> raises Protection.");
        kv(content, skin, "Temple     30t", "2 Monk slots -> raises Faith & Happiness.");

        // ── POPULATION ───────────────────────────────────────────────────────
        h2(content, skin, "POPULATION");
        warn(content, skin, "All 8 start unhoused. Build Dwellings first!");
        body(content, skin, "Idle villagers auto-fill job slots each tick.");
        dim(content, skin,
            "Birth requires Food >= 70, Housing >= 60, Happiness >= 60, costs 40 rice.\n"
            + "Starvation (rice = 0) kills one villager every 3 ticks.");

        // ── POLICIES ─────────────────────────────────────────────────────────
        h2(content, skin, "POLICIES  (bottom-left panel)");
        dim(content, skin, "Duration 5 ticks, cooldown 8 ticks. Only one active at a time.");
        kv(content, skin, "Agriculture", "rice x1.5, farmer tool cost x1.5.");
        kv(content, skin, "Military",    "protection x1.5, samurai upkeep x1.5.");
        kv(content, skin, "Craft",       "production x1.5 for timber/tools/luxury.");

        // ── MARKET TRADING ────────────────────────────────────────────────────
        h2(content, skin, "MARKET TRADING");
        body(content, skin, "Click a placed Market -> 'Open Market' button appears.");
        dim(content, skin, "Exchange rate: give 2 of one resource, receive 1 of another.");

        // ── RANDOM EVENTS ─────────────────────────────────────────────────────
        h2(content, skin, "RANDOM EVENTS");
        body(content, skin, "Events fire automatically each tick (messages in event log).");
        dim(content, skin,
            "Theft, Productivity Spikes, Festivals, Breakthroughs, Accidents.\n"
            + "High Protection reduces theft. High Faith enables Festivals.\n"
            + "Event alerts auto-dismiss when the next tick advances.");

        // ── SAVE / LOAD ───────────────────────────────────────────────────────
        h2(content, skin, "SAVE / LOAD");
        body(content, skin, "Save/Load buttons -> choose one of 5 slots.");
        dim(content, skin,
            "Saves to ~/.daimyosimulator/savegame_N.json.\n"
            + "New resets the village. Load restores a saved game.");

        ScrollPane scroll = new ScrollPane(content, new ScrollPane.ScrollPaneStyle());
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(true);

        getContentTable().pad(14);
        getContentTable().add(scroll).size(600, 460);
        button("Close");
        setMovable(true);
    }

    // ── Layout helpers ────────────────────────────────────────────────────────

    private static void h1(Table t, Skin skin, String text) {
        Label l = new Label(text, skin, "title");
        t.add(l).left().padBottom(3);
        t.row();
    }

    private static void sep(Table t, Skin skin) {
        Label l = new Label("==========================================", skin, "dim");
        t.add(l).left().padBottom(8);
        t.row();
    }

    private static void h2(Table t, Skin skin, String text) {
        Label l = new Label(text, skin, "title");
        t.add(l).left().padTop(10).padBottom(4);
        t.row();
    }

    private static void body(Table t, Skin skin, String text) {
        Label l = new Label(text, skin);
        l.setWrap(true);
        t.add(l).width(WRAP_WIDTH).left().padLeft(10).padBottom(2);
        t.row();
    }

    private static void warn(Table t, Skin skin, String text) {
        Label l = new Label(text, skin, "warning");
        l.setWrap(true);
        t.add(l).width(WRAP_WIDTH).left().padLeft(10).padBottom(2);
        t.row();
    }

    private static void dim(Table t, Skin skin, String text) {
        Label l = new Label(text, skin, "dim");
        l.setWrap(true);
        t.add(l).width(WRAP_WIDTH).left().padLeft(10).padBottom(2);
        t.row();
    }

    private static void kv(Table t, Skin skin, String key, String value) {
        Table row = new Table();
        Label keyLabel = new Label(key, skin, "hint");
        Label valLabel = new Label(value, skin);
        valLabel.setWrap(true);
        row.add(keyLabel).width(148).left().padRight(8);
        row.add(valLabel).width(340).left();
        t.add(row).left().padLeft(10).padBottom(1);
        t.row();
    }
}
