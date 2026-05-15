package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import it.unipd.daimyosimulator.core.app.view.ResourceViewModel;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

import java.util.EnumMap;
import java.util.Map;

public final class ResourcePanel extends Table {
    private final Map<ResourceType, Label> labels = new EnumMap<>(ResourceType.class);

    public ResourcePanel(Skin skin, GameAssetManager assetManager) {
        setBackground(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(assetManager.getUi(assetManager.ui().panelParchment())));
        defaults().pad(2);
        for (ResourceType type : ResourceType.values()) {
            add(new Image(assetManager.getResourceIcon(type))).size(24);
            Label label = new Label("0", skin);
            labels.put(type, label);
            add(label).width(52).left();
        }
    }

    public void refresh(ResourceViewModel resources) {
        labels.get(ResourceType.RICE).setText(String.valueOf(resources.rice()));
        labels.get(ResourceType.TIMBER).setText(String.valueOf(resources.timber()));
        labels.get(ResourceType.TOOLS).setText(String.valueOf(resources.tools()));
        labels.get(ResourceType.LUXURY_GOODS).setText(String.valueOf(resources.luxuryGoods()));
    }
}
