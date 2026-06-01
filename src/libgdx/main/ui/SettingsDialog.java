package gdx.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import core.app.CoreGameFacade;
import core.app.view.VillageSnapshot;
import gdx.assets.GameSoundManager;

import java.util.function.Consumer;

public final class SettingsDialog extends Dialog {

    public SettingsDialog(Skin skin, CoreGameFacade facade,
                          Consumer<VillageSnapshot> snapshotConsumer,
                          Consumer<String> statusConsumer,
                          GameSoundManager soundManager) {
        super("Settings", skin);

        Table content = getContentTable();
        content.pad(16, 20, 8, 20);
        content.defaults().padBottom(8);

        TextButton newGame = new TextButton("New Game", skin);
        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playClick();
                facade.startNewVillage(20, 20);
                snapshotConsumer.accept(facade.applyStarterBuildings());
                statusConsumer.accept("New village created");
                hide();
            }
        });
        content.add(newGame).width(200).height(40);
        content.row();

        TextButton saveGame = new TextButton("Save Game", skin);
        saveGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playClick();
                if (getStage() != null) {
                    new SaveLoadDialog(skin, facade, true, snapshotConsumer, statusConsumer)
                            .show(getStage());
                }
                hide();
            }
        });
        content.add(saveGame).width(200).height(40);
        content.row();

        TextButton loadGame = new TextButton("Load Game", skin);
        loadGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playClick();
                if (getStage() != null) {
                    new SaveLoadDialog(skin, facade, false, snapshotConsumer, statusConsumer)
                            .show(getStage());
                }
                hide();
            }
        });
        content.add(loadGame).width(200).height(40);
        content.row();

        TextButton commands = new TextButton("Commands", skin);
        commands.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.playClick();
                if (getStage() != null) {
                    new CommandsDialog(skin).show(getStage());
                }
                hide();
            }
        });
        content.add(commands).width(200).height(40);

        button("Close");
        setMovable(true);
    }
}
