package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.assets.GameSoundManager;
import it.unipd.daimyosimulator.gdx.input.BuildModeState;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public final class BuildMenu extends Table {
    // Sized so the row content fills the LEFT_PANEL_WIDTH (226) box while sitting inside
    // the rounded panel border (which reserves ~5px each side).
    private static final float BUILD_BUTTON_WIDTH = 184f;
    private static final float COUNT_LABEL_WIDTH = 18f;

    private static final int TIMBER_COST_DWELLING       = 15;
    private static final int TIMBER_COST_RICE_FARM      = 18;
    private static final int TIMBER_COST_RICE_PADDY     = 8;
    private static final int TIMBER_COST_WOODCUTTERS    = 20;
    private static final int TIMBER_COST_MINE           = 25;
    private static final int TIMBER_COST_SMITHY         = 30;
    private static final int TIMBER_COST_WORKSHOP       = 35;
    private static final int TIMBER_COST_MARKET         = 25;
    private static final int TIMBER_COST_GUARD_POST     = 25;
    private static final int TIMBER_COST_TEMPLE         = 30;

    private final Label buildLimitLabel;
    private final Map<BuildingType, Label> countLabels = new EnumMap<>(BuildingType.class);
    private final TextButton demolishButton;
    private final BuildModeState buildModeState;

    private final GameSoundManager soundManager;

    public BuildMenu(Skin skin, GameAssetManager assetManager, BuildModeState buildModeState,
                     Consumer<String> statusConsumer, GameSoundManager soundManager) {
        this.buildModeState = buildModeState;
        this.soundManager = soundManager;
        setBackground(skin.getDrawable("hud-panel"));
        defaults().pad(1);

        // Header row: "BUILD  Builds: x/y" — kept in its own sub-table (spanning both
        // columns) so the wide "Builds" label doesn't stretch the per-row count column,
        // and both ends stay inside the panel border.
        buildLimitLabel = new Label("Builds: -/-", skin, "dim");
        Table header = new Table();
        header.add(new Label("BUILD", skin)).left().expandX();
        header.add(buildLimitLabel).right();
        add(header).colspan(2).fillX().padLeft(6).padRight(4);
        row();

        for (BuildingType type : BuildingType.values()) {
            int cost = costFor(type);

            // 4-column layout (all widths fixed so columns align across every row):
            // [Name — expanding] [cost 24px] [wood icon 13px] [building icon 16px]
            TextButton button = new TextButton(shortName(type), skin);
            // Name takes the leftover space and is centered within it.
            button.getLabelCell().expandX().center().padLeft(4);
            Label costLabel = new Label("" + cost, skin, "hint");
            button.add(costLabel).width(24).right().padRight(1);
            button.add(new Image(assetManager.getResourceIcon(ResourceType.TIMBER))).size(13).padLeft(2).padRight(2);
            button.add(new Image(assetManager.getBuilding(type))).size(16).padLeft(2).padRight(4);
            button.addListener(new TextTooltip(tooltipFor(type), skin));
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    soundManager.playClick();
                    buildModeState.enter(type);
                    statusConsumer.accept("Build mode: " + shortName(type) + " – click grid to place");
                }
            });

            // Count label: [n]
            Label countLabel = new Label("[0]", skin, "dim");
            countLabels.put(type, countLabel);

            add(button).width(BUILD_BUTTON_WIDTH).height(32).left().padLeft(5);
            add(countLabel).width(COUNT_LABEL_WIDTH).right();
            row();
        }

        // Demolish button.
        demolishButton = new TextButton("Demolish", skin, "demolish");
        demolishButton.addListener(new TextTooltip(
                "Demolish mode: click any building on the grid to remove it.\n"
                + "No timber refund. Workers become idle. Right-click to cancel.",
                skin));
        demolishButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                soundManager.playClick();
                if (((TextButton) actor).isChecked()) {
                    buildModeState.enterDemolish();
                    statusConsumer.accept("Demolish mode – click a building or forest to remove it");
                } else {
                    buildModeState.clear();
                    statusConsumer.accept("Demolish mode cancelled");
                }
            }
        });
        // Aligned to the building "block" (same width/left edge as the build buttons).
        add(demolishButton).colspan(2).width(BUILD_BUTTON_WIDTH).height(32).left().pad(1).padLeft(5);
        row();
    }

    /** Called each time the snapshot changes. Updates build-limit header and per-type counts. */
    public void refresh(VillageSnapshot snapshot) {
        buildLimitLabel.setText("Builds: " + snapshot.buildsThisTick() + "/" + snapshot.maxBuildsPerTick());
        for (BuildingType type : BuildingType.values()) {
            long count = snapshot.cells().stream()
                    .filter(c -> c.building() != null && c.building().type() == type)
                    .count();
            Label lbl = countLabels.get(type);
            if (lbl != null) {
                lbl.setText("[" + count + "]");
            }
        }
        demolishButton.setProgrammaticChangeEvents(false);
        demolishButton.setChecked(buildModeState.isDemolishMode());
        demolishButton.setProgrammaticChangeEvents(true);
    }

    private static String shortName(BuildingType type) {
        return switch (type) {
            case DWELLING      -> "Dwelling";
            case RICE_FARM     -> "Farm";
            case RICE_PADDY    -> "Paddy";
            case WOODCUTTERS_HUT -> "Woodcutter";
            case MINE          -> "Mine";
            case SMITHY        -> "Smithy";
            case WORKSHOP      -> "Workshop";
            case MARKET        -> "Market";
            case GUARD_POST    -> "Guard";
            case TEMPLE        -> "Temple";
        };
    }

    private static int costFor(BuildingType type) {
        return switch (type) {
            case DWELLING      -> TIMBER_COST_DWELLING;
            case RICE_FARM     -> TIMBER_COST_RICE_FARM;
            case RICE_PADDY    -> TIMBER_COST_RICE_PADDY;
            case WOODCUTTERS_HUT -> TIMBER_COST_WOODCUTTERS;
            case MINE          -> TIMBER_COST_MINE;
            case SMITHY        -> TIMBER_COST_SMITHY;
            case WORKSHOP      -> TIMBER_COST_WORKSHOP;
            case MARKET        -> TIMBER_COST_MARKET;
            case GUARD_POST    -> TIMBER_COST_GUARD_POST;
            case TEMPLE        -> TIMBER_COST_TEMPLE;
        };
    }

    private static String tooltipFor(BuildingType type) {
        return switch (type) {
            case DWELLING ->
                "Dwelling - Cost: 15 timber\n"
                + "Houses 4 villagers. Unhoused villagers cannot work.\n"
                + "Build more to allow population growth.";
            case RICE_FARM ->
                "Rice Farm – Cost: 18 timber\n"
                + "Provides 3 Rice Farmer job slots.\n"
                + "Each adjacent Rice Paddy adds +5 rice/tick per farmer pair.\n"
                + "Must be placed next to Rice Paddies to produce.";
            case RICE_PADDY ->
                "Rice Paddy – Cost: 8 timber\n"
                + "Boosts adjacent Rice Farm output. No workers needed.\n"
                + "Place next to a Farm for production.";
            case WOODCUTTERS_HUT ->
                "Woodcutter's Hut – Cost: 20 timber\n"
                + "Provides 3 Woodcutter slots. Produces 3 timber/tick per valid hut.\n"
                + "MUST be placed adjacent to a Forest tile.";
            case MINE ->
                "Mine – Cost: 25 timber\n"
                + "Required prerequisite for Smithy and Workshop.\n"
                + "Provides no workers directly.";
            case SMITHY ->
                "Smithy – Cost: 30 timber\n"
                + "Provides 2 Blacksmith slots. Produces 2 tools/tick per blacksmith.\n"
                + "Requires a Mine anywhere on the map.";
            case WORKSHOP ->
                "Workshop – Cost: 35 timber\n"
                + "Provides 2 Artisan slots. Produces 2 luxury goods every 3 ticks.\n"
                + "Requires a Mine anywhere on the map.";
            case MARKET ->
                "Market – Cost: 25 timber\n"
                + "Provides 2 Trader slots. Enables resource trading.\n"
                + "Click a placed Market on the grid to open the trade menu.\n"
                + "Exchange rate: 2 of one resource for 1 of another.";
            case GUARD_POST ->
                "Guard Post – Cost: 25 timber\n"
                + "Provides 2 Samurai slots. Increases village Protection.\n"
                + "Higher protection reduces theft event probability.";
            case TEMPLE ->
                "Temple – Cost: 30 timber\n"
                + "Provides 2 Monk slots. Increases Faith and Happiness.\n"
                + "Enables Religious Festival random events.";
        };
    }
}
