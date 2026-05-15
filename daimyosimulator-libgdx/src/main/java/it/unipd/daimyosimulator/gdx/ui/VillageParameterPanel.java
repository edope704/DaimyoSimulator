package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import it.unipd.daimyosimulator.core.app.view.VillageParametersViewModel;

public final class VillageParameterPanel extends Table {
    private final Label label;

    public VillageParameterPanel(Skin skin) {
        label = new Label("", skin);
        add(label).left();
    }

    public void refresh(VillageParametersViewModel parameters) {
        label.setText("Hap " + parameters.happiness()
                + "  Prot " + parameters.protection()
                + "  Food " + parameters.food()
                + "  Faith " + parameters.faith()
                + "  House " + parameters.housing()
                + "  Craft " + parameters.craftsmanship());
    }
}
