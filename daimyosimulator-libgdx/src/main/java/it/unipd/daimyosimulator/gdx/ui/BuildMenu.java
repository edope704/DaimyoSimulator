package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.input.BuildModeState;

import java.util.function.Consumer;

public final class BuildMenu extends Table {
    public BuildMenu(Skin skin, GameAssetManager assetManager, BuildModeState buildModeState, Consumer<String> statusConsumer) {
        setBackground(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(assetManager.getUi(assetManager.ui().panelWood())));
        defaults().pad(2).height(42);
        for (BuildingType type : BuildingType.values()) {
            TextButton button = new TextButton(shortName(type), skin);
            button.add(new com.badlogic.gdx.scenes.scene2d.ui.Image(assetManager.getBuilding(type))).size(30).padRight(4);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    buildModeState.enter(type);
                    statusConsumer.accept("Build mode: " + type);
                }
            });
            add(button).width(132);
            row();
        }
    }

    private String shortName(BuildingType type) {
        return switch (type) {
            case DWELLING -> "Dwelling";
            case RICE_FARM -> "Farm";
            case RICE_PADDY -> "Paddy";
            case WOODCUTTERS_HUT -> "Woodcutter";
            case MINE -> "Mine";
            case SMITHY -> "Smithy";
            case WORKSHOP -> "Workshop";
            case MARKET -> "Market";
            case GUARD_POST -> "Guard";
            case TEMPLE -> "Temple";
        };
    }
}
