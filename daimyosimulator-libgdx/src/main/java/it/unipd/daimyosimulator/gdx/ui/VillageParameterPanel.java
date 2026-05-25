package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import it.unipd.daimyosimulator.core.app.view.VillageParametersViewModel;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.assets.ParameterType;

import java.util.EnumMap;
import java.util.Map;

public final class VillageParameterPanel extends Table {
    private final Skin skin;
    private final Map<ParameterType, Label> labels = new EnumMap<>(ParameterType.class);

    public VillageParameterPanel(Skin skin, GameAssetManager assetManager) {
        this.skin = skin;
        setBackground(skin.getDrawable("hud-panel"));
        defaults().pad(2);
        addParameter(skin, assetManager, ParameterType.HAPPINESS);
        addParameter(skin, assetManager, ParameterType.PROTECTION);
        row();
        addParameter(skin, assetManager, ParameterType.FOOD);
        addParameter(skin, assetManager, ParameterType.FAITH);
        row();
        addParameter(skin, assetManager, ParameterType.HOUSING);
        addParameter(skin, assetManager, ParameterType.CRAFTSMANSHIP);
    }

    public void refresh(VillageParametersViewModel parameters) {
        labels.get(ParameterType.HAPPINESS).setText(String.valueOf(parameters.happiness()));
        labels.get(ParameterType.PROTECTION).setText(String.valueOf(parameters.protection()));
        labels.get(ParameterType.FOOD).setText(String.valueOf(parameters.food()));
        labels.get(ParameterType.FAITH).setText(String.valueOf(parameters.faith()));
        labels.get(ParameterType.HOUSING).setText(String.valueOf(parameters.housing()));
        labels.get(ParameterType.CRAFTSMANSHIP).setText(String.valueOf(parameters.craftsmanship()));
    }

    private void addParameter(Skin skin, GameAssetManager assetManager, ParameterType type) {
        Image icon = new Image(assetManager.getParameterIcon(type));
        Label label = new Label("0", skin);
        labels.put(type, label);

        icon.addListener(new InputListener() {
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { return true; }
            @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) { showParamInfo(type); }
        });
        label.addListener(new InputListener() {
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { return true; }
            @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) { showParamInfo(type); }
        });

        add(icon).size(24);
        add(label).width(42).left();
    }

    private void showParamInfo(ParameterType type) {
        if (getStage() == null) return;
        Dialog dialog = new Dialog(paramName(type), skin);
        dialog.getContentTable().pad(16);
        Label desc = new Label(paramDescription(type), skin);
        desc.setWrap(true);
        dialog.getContentTable().add(desc).width(260).left();
        dialog.button("OK");
        dialog.show(getStage());
        getStage().cancelTouchFocus();
    }

    private static String paramName(ParameterType type) {
        return switch (type) {
            case HAPPINESS     -> "Happiness";
            case PROTECTION    -> "Protection";
            case FOOD          -> "Food";
            case FAITH         -> "Faith";
            case HOUSING       -> "Housing";
            case CRAFTSMANSHIP -> "Craftsmanship";
            default            -> type.name();
        };
    }

    private static String paramDescription(ParameterType type) {
        return switch (type) {
            case HAPPINESS ->
                "Overall villager morale. Influenced by food supply, faith, housing, " +
                "and recent events. Low happiness raises the chance of unrest events " +
                "and reduces worker productivity.";
            case PROTECTION ->
                "Village defence rating. Raised by Guard Posts and active Samurai. " +
                "Higher protection reduces the frequency and severity of theft and " +
                "bandit raid events.";
            case FOOD ->
                "Measures how well the village is fed. Driven by Rice Farms and " +
                "Rice Paddies. If food drops to zero, villagers become unhappy and " +
                "unhoused workers stop producing.";
            case FAITH ->
                "Spiritual wellbeing of the village. Raised by Temples and active " +
                "Monks. High faith boosts happiness and unlocks Religious Festival " +
                "random events.";
            case HOUSING ->
                "Proportion of villagers with a home. Each Dwelling houses 4 " +
                "villagers. Unhoused villagers cannot be assigned to work roles, " +
                "capping your workforce.";
            case CRAFTSMANSHIP ->
                "Reflects the quality of tools and goods produced. Raised by active " +
                "Smithies and Workshops. Higher craftsmanship improves tool output " +
                "and enables better trade terms at the Market.";
            default -> "No information available.";
        };
    }
}
