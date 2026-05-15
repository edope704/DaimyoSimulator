package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.gdx.input.BuildModeState;

import java.util.function.Consumer;

public final class BuildMenu extends Table {
    public BuildMenu(Skin skin, BuildModeState buildModeState, Consumer<String> statusConsumer) {
        defaults().pad(2).height(28);
        for (BuildingType type : BuildingType.values()) {
            TextButton button = new TextButton(shortName(type), skin);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    buildModeState.enter(type);
                    statusConsumer.accept("Build mode: " + type);
                }
            });
            add(button).width(94);
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
