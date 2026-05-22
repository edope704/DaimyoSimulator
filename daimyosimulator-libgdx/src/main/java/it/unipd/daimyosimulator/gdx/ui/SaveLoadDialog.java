package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.app.CoreGameFacade;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;

import java.util.function.Consumer;

/**
 * Dialog for choosing one of five named save slots.
 * Used for both Save and Load operations depending on the {@code isSave} flag.
 */
public final class SaveLoadDialog extends Dialog {

    public SaveLoadDialog(Skin skin, CoreGameFacade facade, boolean isSave,
                          Consumer<VillageSnapshot> snapshotConsumer,
                          Consumer<String> statusConsumer) {
        super(isSave ? "Save Game – Choose Slot" : "Load Game – Choose Slot", skin);

        Table content = getContentTable();
        content.pad(12);
        content.defaults().fillX().pad(3);

        for (CoreGameFacade.SaveSlotInfo info : CoreGameFacade.listSaveSlots()) {
            TextButton slotBtn = new TextButton(info.label(), skin);
            final int slot = info.slot();

            slotBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    if (isSave) {
                        var result = facade.saveVillage(slot);
                        statusConsumer.accept(result.message());
                    } else {
                        if (!info.exists()) {
                            statusConsumer.accept("Slot " + slot + " is empty.");
                        } else {
                            var result = facade.loadVillage(slot);
                            statusConsumer.accept(result.message());
                            if (result.snapshot() != null) {
                                snapshotConsumer.accept(result.snapshot());
                            }
                        }
                    }
                    hide();
                }
            });
            content.add(slotBtn).height(36);
            content.row();

            if (!isSave && !info.exists()) {
                slotBtn.setDisabled(true);
            }
        }

        Label note = new Label("Saves are stored in ~/.daimyosimulator/", skin, "dim");
        content.add(note).padTop(6);
        content.row();

        button("Cancel");
        setMovable(true);
    }
}
