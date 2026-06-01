package gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import core.app.CoreGameFacade;
import core.app.view.VillageSnapshot;
import gdx.assets.GameAssetManager;

import java.util.function.Consumer;

public final class MenuOverlay extends Table {
    public MenuOverlay(Skin skin, GameAssetManager assetManager, CoreGameFacade facade,
                       Consumer<VillageSnapshot> snapshotConsumer,
                       Consumer<String> statusConsumer) {
        setBackground(skin.getDrawable("hud-panel"));

        TextButton newButton  = new TextButton("New",  skin);
        TextButton saveButton = new TextButton("Save", skin);
        TextButton loadButton = new TextButton("Load", skin);

        newButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                snapshotConsumer.accept(facade.startNewVillage(20, 20));
                statusConsumer.accept("New village created");
            }
        });

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (getStage() != null) {
                    new SaveLoadDialog(skin, facade, true, snapshotConsumer, statusConsumer)
                            .show(getStage());
                }
            }
        });

        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (getStage() != null) {
                    new SaveLoadDialog(skin, facade, false, snapshotConsumer, statusConsumer)
                            .show(getStage());
                }
            }
        });

        defaults().pad(2);
        add(newButton).width(58);
        add(saveButton).width(58);
        add(loadButton).width(58);
    }
}
