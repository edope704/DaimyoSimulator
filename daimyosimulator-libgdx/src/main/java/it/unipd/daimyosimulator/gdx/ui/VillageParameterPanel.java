package it.unipd.daimyosimulator.gdx.ui;

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
    private final Map<ParameterType, Label> labels = new EnumMap<>(ParameterType.class);

    public VillageParameterPanel(Skin skin, GameAssetManager assetManager) {
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
        add(new Image(assetManager.getParameterIcon(type))).size(24);
        Label label = new Label("0", skin);
        labels.put(type, label);
        add(label).width(42).left();
    }
}
