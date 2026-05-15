package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import it.unipd.daimyosimulator.core.app.view.PopulationViewModel;

public final class PopulationPanel extends Table {
    private final Label label;

    public PopulationPanel(Skin skin) {
        label = new Label("", skin);
        add(label).left();
    }

    public void refresh(PopulationViewModel population) {
        label.setText("Pop " + population.total()
                + "  Idle " + population.idle()
                + "  Employed " + population.employed()
                + "  Unhoused " + population.unhoused());
    }
}
