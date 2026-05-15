package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import it.unipd.daimyosimulator.core.app.view.PopulationViewModel;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;
import it.unipd.daimyosimulator.gdx.assets.ParameterType;

public final class PopulationPanel extends Table {
    private final Label label;

    public PopulationPanel(Skin skin, GameAssetManager assetManager) {
        setBackground(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(assetManager.getUi(assetManager.ui().panelParchment())));
        label = new Label("", skin);
        add(new Image(assetManager.getParameterIcon(ParameterType.POPULATION))).size(24).padRight(4);
        add(label).left();
    }

    public void refresh(PopulationViewModel population) {
        label.setText("Pop " + population.total()
                + "  Idle " + population.idle()
                + "  Employed " + population.employed()
                + "  Unhoused " + population.unhoused());
    }
}
