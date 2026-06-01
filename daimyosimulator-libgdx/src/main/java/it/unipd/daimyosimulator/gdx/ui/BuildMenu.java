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

import it.unipd.daimyosimulator.core.service.ProgressiveCostCalculator;

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
    private final Map<BuildingType, Label> costLabels  = new EnumMap<>(BuildingType.class);
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
            // [building icon 16px] [Name — expanding/centered] [cost 24px] [wood icon 13px]
            TextButton button = new TextButton(shortName(type), skin);
            Label nameLabel = button.getLabel();
            button.clearChildren();
            // Building icon first, then the centered name.
            button.add(new Image(assetManager.getBuilding(type))).size(16).padLeft(4).padRight(4);
            button.add(nameLabel).expandX().center();
            // Cost number + wood icon on the right, nudged inward from the edge.
            Label costLabel = new Label("" + cost, skin, "hint");
            costLabels.put(type, costLabel);
            button.add(costLabel).width(24).right().padRight(0);
            button.add(new Image(assetManager.getResourceIcon(ResourceType.TIMBER))).size(13).padLeft(0).padRight(8);
            button.addListener(new TextTooltip(tooltipFor(type), skin));
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    soundManager.playClick();
                    buildModeState.enter(type);
                    statusConsumer.accept("Build mode: " + shortName(type) + " - click grid to place");
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
                "Demolish mode: click any building or forest on the grid to remove it.\n"
                + "Building demolished: +5 timber refund. Forest cleared: +10 timber.\n"
                + "Workers become idle. Right-click to cancel.",
                skin));
        demolishButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                soundManager.playClick();
                if (((TextButton) actor).isChecked()) {
                    buildModeState.enterDemolish();
                    statusConsumer.accept("Demolish mode - click a building or forest to remove it");
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

    /** Called each time the snapshot changes. Updates build-limit header, per-type counts, and scaled costs. */
    public void refresh(VillageSnapshot snapshot) {
        buildLimitLabel.setText("Builds: " + snapshot.buildsThisTick() + "/" + snapshot.maxBuildsPerTick());
        for (BuildingType type : BuildingType.values()) {
            long count = snapshot.cells().stream()
                    .filter(c -> c.building() != null && c.building().type() == type)
                    .count();
            Label countLbl = countLabels.get(type);
            if (countLbl != null) countLbl.setText("[" + count + "]");

            Label costLbl = costLabels.get(type);
            if (costLbl != null) {
                int scaled = ProgressiveCostCalculator.scaledCost(type, (int) count, costFor(type));
                costLbl.setText("" + scaled);
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
                "Rice Farm - Cost: 18 timber\n"
                + "Provides 3 Rice Farmer job slots.\n"
                + "Each adjacent Rice Paddy adds +5 rice/tick per farmer pair.\n"
                + "Must be placed next to Rice Paddies to produce.";
            case RICE_PADDY ->
                "Rice Paddy - Cost: 8 timber\n"
                + "Boosts adjacent Rice Farm output. No workers needed.\n"
                + "Place next to a Farm for production.";
            case WOODCUTTERS_HUT ->
                "Woodcutter's Hut - Cost: 20 timber\n"
                + "Provides 3 Woodcutter slots. Produces 1 timber/tick per woodcutter.\n"
                + "MUST be placed adjacent to a Forest tile.\n"
                + "Note: timber output is 1/3 of early rates - place multiple huts.";
            case MINE ->
                "Mine - Cost: 25 timber\n"
                + "Required prerequisite for Smithy and Workshop.\n"
                + "Provides no workers directly.";
            case SMITHY ->
                "Smithy - Cost: 30 timber\n"
                + "Provides 2 Blacksmith slots. Produces 2 tools/tick per blacksmith.\n"
                + "Must be placed adjacent to a Mine to produce. Dims if Mine is absent.";
            case WORKSHOP ->
                "Workshop - Cost: 35 timber\n"
                + "Provides 2 Artisan slots. Produces 2 luxury goods every tick.\n"
                + "Must be placed adjacent to a Mine to produce. Dims if Mine is absent.";
            case MARKET ->
                "Market - Cost: 25 timber\n"
                + "Enables resource trading. Each Market adds +10 max trade capacity.\n"
                + "After any trade a 10-tick global cooldown locks all Markets.\n"
                + "Rates (give:receive): Rice 5:1 Timber, 15:1 Tools, 30:1 Luxury.\n"
                + "Timber: 1:5 Rice, 10:1 Tools, 30:1 Luxury.\n"
                + "Tools: 1:15 Rice, 1:10 Timber, 20:1 Luxury.";
            case GUARD_POST ->
                "Guard Post - Cost: 25 timber\n"
                + "Provides 2 Samurai slots.\n"
                + "Security = 0% with no Samurai. Grants NO flat protection bonus.\n"
                + "Formula: Security = Samurai / Population x 8 x 100  (cap 100%).\n"
                + "Optimal: 1 Samurai per 8 citizens -> 100% security.\n"
                + "High security reduces theft event chance.";
            case TEMPLE ->
                "Temple - Cost: 30 timber\n"
                + "Provides 2 Monk slots.\n"
                + "Culture (Faith) = 0% with no Monks. Grants NO flat faith bonus.\n"
                + "Formula: Faith = Monks / Population x 12 x 100  (cap 100%).\n"
                + "Optimal: 1 Monk per 12 citizens -> 100% faith.\n"
                + "High faith raises Happiness and enables Religious Festival events.";
        };
    }
}
