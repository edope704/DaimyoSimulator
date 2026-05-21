package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;

/** Static tutorial/help modal. Press "?" in the top bar to open. */
public final class TutorialDialog extends Dialog {
    private static final String TEXT =
        "DAIMYO SIMULATOR – QUICK GUIDE\n"
        + "═══════════════════════════════════════════\n\n"
        + "GOAL\n"
        + "Grow your village, feed your people, and survive random events.\n\n"
        + "CONTROLS\n"
        + "  Left-click building button → enter build mode\n"
        + "  Left-click grid tile       → place building / inspect cell\n"
        + "  Right-click / Escape       → cancel build or demolish mode\n"
        + "  Next button                → advance one tick manually\n"
        + "  Pause button               → toggle auto-advance\n"
        + "  Speed button               → cycle 1×  2×  4× speed\n"
        + "  Demolish button            → remove a building (no refund)\n"
        + "  F3                         → toggle debug grid overlay\n\n"
        + "RESOURCES\n"
        + "  Rice     – consumed 2/tick per villager. Runs out → starvation.\n"
        + "  Timber   – spent to construct buildings.\n"
        + "  Tools    – consumed by Farmers & Samurai each tick.\n"
        + "  Luxury   – consumed by Samurai & Monks each tick.\n"
        + "  YELLOW number = low stock  |  RED = critical / danger!\n\n"
        + "BUILD LIMIT\n"
        + "  You may only place 2 buildings per tick.\n"
        + "  The counter 'Builds: x/y' in the left panel resets each tick.\n\n"
        + "BUILDINGS (cost in timber ■)\n"
        + "  Dwelling  15■  – houses 4 villagers. Unhoused cannot work.\n"
        + "  Farm      18■  – 3 Farmer slots. Needs adjacent Paddies.\n"
        + "  Paddy      8■  – boosts adjacent Farm. +5 rice/tick per pair.\n"
        + "  Woodcutter 20■ – 3 Woodcutter slots. MUST touch a Forest.\n"
        + "  Mine       25■ – prerequisite for Smithy & Workshop.\n"
        + "  Smithy     30■ – 2 Blacksmith slots → +2 Tools/tick.\n"
        + "  Workshop   35■ – 2 Artisan slots → +2 Luxury/3 ticks.\n"
        + "  Market     25■ – 2 Trader slots. Enables resource trading.\n"
        + "  Guard Post 25■ – 2 Samurai slots → raises Protection.\n"
        + "  Temple     30■ – 2 Monk slots → raises Faith & Happiness.\n\n"
        + "POPULATION\n"
        + "  All 8 start unhoused. Build Dwellings first!\n"
        + "  Idle villagers auto-fill job slots each tick.\n"
        + "  Birth requires Food ≥ 70, Housing ≥ 60, Happiness ≥ 60, costs 40 rice.\n"
        + "  Starvation (rice = 0) kills one villager every 3 ticks.\n\n"
        + "POLICIES (bottom-left)\n"
        + "  Duration 5 ticks, cooldown 8 ticks. Only one active at a time.\n"
        + "  Agriculture – rice ×1.5, farmer tool cost ×1.5.\n"
        + "  Military    – protection ×1.5, samurai upkeep ×1.5.\n"
        + "  Craft       – production ×1.5 for timber/tools/luxury.\n\n"
        + "MARKET TRADING\n"
        + "  Click a placed Market → 'Open Market' button appears.\n"
        + "  Exchange rate: give 2 of one resource, receive 1 of another.\n\n"
        + "RANDOM EVENTS\n"
        + "  Events fire automatically each tick (messages appear bottom-right).\n"
        + "  Theft, Productivity Spikes, Festivals, Breakthroughs, Accidents.\n"
        + "  High Protection reduces theft. High Faith enables Festivals.\n\n"
        + "SAVE / LOAD\n"
        + "  Auto-saves to ~/.daimyosimulator/savegame.json via Save button.\n"
        + "  New resets the village. Load restores last save.\n";

    public TutorialDialog(Skin skin) {
        super("Help & Tutorial", skin);

        Label textLabel = new Label(TEXT, skin);
        textLabel.setWrap(false);

        Table content = new Table();
        content.add(textLabel).left().pad(10);

        // Use a style-less ScrollPane to avoid needing a ScrollPaneStyle in the skin.
        ScrollPane scroll = new ScrollPane(content, new ScrollPaneStyle());
        scroll.setScrollingDisabled(true, false);

        getContentTable().add(scroll).size(580, 420).pad(8);

        button("Close");
        setMovable(true);
    }
}
