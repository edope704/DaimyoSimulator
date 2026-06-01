package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * In-game tutorial. Written in clear, thematic language aimed at a new player.
 * No backend/formula jargon — everything is described in terms of what the player sees and does.
 */
public final class TutorialDialog extends Dialog {

    private static final int WRAP_WIDTH = 500;

    public TutorialDialog(Skin skin) {
        super("Help & Tutorial", skin);

        Table content = new Table();
        content.top().left();
        content.defaults().left();

        h1(content, skin, "DAIMYO SIMULATOR  - A FEUDAL VILLAGE GUIDE");
        sep(content, skin);

        // ── WHAT IS THIS GAME ────────────────────────────────────────────────
        h2(content, skin, "YOUR ROLE");
        body(content, skin,
            "You are a Daimyo, a feudal lord tasked with building a thriving settlement\n"
            + "from a handful of villagers and a forest clearing. Gather resources, raise\n"
            + "buildings, grow your population, and weather random events.");
        dim(content, skin,
            "Your village starts with 2 Dwellings and a Woodcutter's Hut already in place\n"
            + "so your first settlers have a home and a source of wood from day one.");

        // ── CONTROLS ─────────────────────────────────────────────────────────
        h2(content, skin, "CONTROLS");
        kv(content, skin, "Left-click a building",  "enter build mode for that structure");
        kv(content, skin, "Left-click the map",     "place the selected building or inspect a tile");
        kv(content, skin, "Right-click or Esc",     "cancel placement or demolish mode");
        kv(content, skin, "Demolish button",         "remove a tile (returns some timber)");
        kv(content, skin, "Next / Pause / Speed",    "advance time, pause, or change tick speed");
        kv(content, skin, "Gear icon (top left)",    "save, load, or start a new village");
        kv(content, skin, "F3",                      "toggle the debug grid overlay");

        // ── THE FOUR RESOURCES ────────────────────────────────────────────────
        h2(content, skin, "RESOURCES  (top bar)");
        warn(content, skin, "Watch these carefully: yellow means low, red means danger!");
        kv(content, skin, "Rice",         "food for your people. Every villager eats 2 rice per turn.");
        kv(content, skin, "Timber",       "the main building material. Everything costs timber to build.");
        kv(content, skin, "Tools",        "used by Farmers and Samurai each turn.");
        kv(content, skin, "Luxury Goods", "consumed by Samurai and Monks each turn.");
        dim(content, skin,
            "If a resource hits zero the WARNINGS panel on the right immediately alerts you.\n"
            + "If rice runs out entirely, a villager will die every few turns from starvation.");

        // ── BUILD LIMIT ───────────────────────────────────────────────────────
        h2(content, skin, "CONSTRUCTION LIMIT");
        warn(content, skin, "You can only place 2 buildings per turn.");
        dim(content, skin,
            "The counter in the build panel resets each time a new turn begins.\n"
            + "Costs also rise slightly each time you build more of the same type,\n"
            + "so plan ahead rather than spamming a single structure.");

        // ── BUILDINGS ─────────────────────────────────────────────────────────
        h2(content, skin, "BUILDINGS  (base timber cost)");
        kv(content, skin, "Dwelling   15t", "houses 4 people. Homeless villagers cannot work.");
        kv(content, skin, "Farm       18t", "puts 3 Rice Farmers to work. Needs Rice Paddies next to it.");
        kv(content, skin, "Paddy       8t", "boosts a neighbouring Farm. Place right next to a Farm.");
        kv(content, skin, "Woodcutter 20t", "3 workers chop timber. MUST touch a Forest edge.");
        kv(content, skin, "Mine       25t", "unlocks advanced crafting nearby.");
        kv(content, skin, "Smithy     30t", "2 Blacksmiths make tools; only works next to a Mine.");
        kv(content, skin, "Workshop   35t", "2 Artisans make luxury goods; only works next to a Mine.");
        kv(content, skin, "Market     25t", "enables trading resources. Each Market adds +10 trade capacity.");
        kv(content, skin, "Guard Post 25t", "2 Samurai keep the peace and raise village security.");
        kv(content, skin, "Temple     30t", "2 Monks raise community faith and happiness.");
        dim(content, skin,
            "Smithies and Workshops appear dimmed when no Mine is directly beside them.\n"
            + "An alert will warn you if you place one too far from a Mine.\n"
            + "Rice Paddies also dim and warn if no Farm is adjacent.");

        // ── POPULATION ───────────────────────────────────────────────────────
        h2(content, skin, "POPULATION");
        warn(content, skin, "Build Dwellings first! Homeless villagers are idle and do not work.");
        body(content, skin,
            "Every turn, idle villagers automatically take up available job slots.\n"
            + "Your village grows whenever rice is plentiful. A good food surplus\n"
            + "means new citizens are born frequently. The richer the surplus, the faster\n"
            + "new arrivals join your community.");
        dim(content, skin,
            "Rice is the only requirement for growth. Keep those farms producing!\n"
            + "Deaths occur when rice reaches zero and starvation sets in.");

        // ── SECURITY & CULTURE ────────────────────────────────────────────────
        h2(content, skin, "SECURITY & CULTURE");
        body(content, skin,
            "Security depends entirely on how many Samurai protect your people.\n"
            + "A good balance is one Samurai for every eight citizens.\n"
            + "A well-protected village suffers far fewer theft events.");
        body(content, skin,
            "Culture (Faith) depends on your Monks relative to the population.\n"
            + "Aim for one Monk for every twelve citizens to keep faith high.\n"
            + "High faith lifts happiness and makes festive events more likely.");
        dim(content, skin,
            "Both values start at zero without any qualified workers, regardless of\n"
            + "how many Guard Posts or Temples you have built.\n"
            + "The Military Policy boosts security by 50% while active.");

        // ── POLICIES ─────────────────────────────────────────────────────────
        h2(content, skin, "EDICTS  (bottom-left panel)");
        dim(content, skin, "Lasts 5 turns, then goes on an 8-turn cooldown. One active at a time.");
        kv(content, skin, "Agricultural Edict", "rice output x1.5, but Farmers use more tools.");
        kv(content, skin, "Military Edict",     "security x1.5, but Samurai consume more.");
        kv(content, skin, "Craft Edict",        "timber, tools, and luxury output x1.5.");

        // ── MARKET TRADING ────────────────────────────────────────────────────
        h2(content, skin, "TRADING AT THE MARKET");
        warn(content, skin, "Every trade locks the Market for 10 turns. Plan carefully!");
        body(content, skin,
            "Select a placed Market on the map, then click 'Open Market'.\n"
            + "Each Market building you own adds 10 units of trading capacity.\n"
            + "Exchange rates heavily favour rarer materials; luxury goods are\n"
            + "worth far more rice than timber, for example.");
        dim(content, skin,
            "Rough exchange guide:\n"
            + "  Spend 5 rice to gain 1 timber\n"
            + "  Spend 15 rice to gain 1 tool\n"
            + "  Spend 30 rice to gain 1 luxury good\n"
            + "  Trade 1 tool away and receive 10 timber in return\n"
            + "  Trading luxury goods yields the best return on any resource.");

        // ── RANDOM EVENTS ─────────────────────────────────────────────────────
        h2(content, skin, "EVENTS");
        body(content, skin,
            "From time to time something unexpected happens in the village.\n"
            + "A pop-up appears describing what occurred. It closes automatically\n"
            + "when the next turn advances.");
        dim(content, skin,
            "Thieves strike more often in unprotected villages.\n"
            + "A faithful community may hold a Religious Festival, boosting happiness.\n"
            + "Skilled craftsmen sometimes achieve a breakthrough, producing bonus goods.\n"
            + "Workshops can occasionally suffer accidents. Keep your stock healthy.");

        // ── SAVE / LOAD ───────────────────────────────────────────────────────
        h2(content, skin, "SAVING YOUR VILLAGE");
        body(content, skin,
            "Open the gear icon in the top bar and choose Save Game, Load Game,\n"
            + "or New Game. Five save slots are available.");
        dim(content, skin,
            "Your village is saved exactly as it stands, including any active\n"
            + "Market cooldown, building counts, and resource totals.");

        ScrollPane scroll = new ScrollPane(content, new ScrollPane.ScrollPaneStyle());
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(true);
        // Prevent content from bouncing/bleeding past the viewport edges.
        scroll.setOverscroll(false, false);
        scroll.setClamp(true);

        getContentTable().pad(14);
        getContentTable().add(scroll).size(600, 460);
        button("Close");
        setMovable(true);

        // Align the window title with the body content (which is inset by pad(14)).
        getTitleTable().padLeft(14);
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
