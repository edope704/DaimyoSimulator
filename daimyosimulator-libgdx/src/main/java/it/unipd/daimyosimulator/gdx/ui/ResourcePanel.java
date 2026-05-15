package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import it.unipd.daimyosimulator.core.app.view.ResourceViewModel;

public final class ResourcePanel extends Table {
    private final Label label;

    public ResourcePanel(Skin skin) {
        label = new Label("", skin);
        add(label).left();
    }

    public void refresh(ResourceViewModel resources) {
        label.setText("Rice " + resources.rice()
                + "  Timber " + resources.timber()
                + "  Tools " + resources.tools()
                + "  Luxury " + resources.luxuryGoods());
    }
}
