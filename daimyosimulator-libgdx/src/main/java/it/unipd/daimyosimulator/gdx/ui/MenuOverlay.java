package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

import java.util.function.Consumer;

public final class MenuOverlay extends Table {
    public MenuOverlay(Skin skin, GameAssetManager assetManager, CoreGameFacade facade, Consumer<VillageSnapshot> snapshotConsumer,
                       Consumer<String> statusConsumer) {
        setBackground(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(assetManager.getUi(assetManager.ui().panelWood())));
        TextButton newButton = new TextButton("New", skin);
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
                statusConsumer.accept(facade.saveVillage(CoreGameFacade.defaultSavePath()).message());
            }
        });
        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                var result = facade.loadVillage(CoreGameFacade.defaultSavePath());
                statusConsumer.accept(result.message());
                if (result.snapshot() != null) {
                    snapshotConsumer.accept(result.snapshot());
                }
            }
        });
        defaults().pad(2);
        add(newButton).width(58);
        add(saveButton).width(58);
        add(loadButton).width(58);
    }
}
