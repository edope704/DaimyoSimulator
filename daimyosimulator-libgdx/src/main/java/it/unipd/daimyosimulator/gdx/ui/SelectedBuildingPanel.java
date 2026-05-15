package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;

public final class SelectedBuildingPanel extends Table {
    private final Label label;

    public SelectedBuildingPanel(Skin skin) {
        label = new Label("No selection", skin);
        add(label).left().width(360);
    }

    public void refresh(CellViewModel cell) {
        if (cell == null) {
            label.setText("No selection");
        } else if (cell.building() != null) {
            label.setText("Selected " + cell.position() + "  " + cell.building().displayName()
                    + "  Jobs " + cell.building().jobSlots());
        } else if (cell.naturalFeature() != null) {
            label.setText("Selected " + cell.position() + "  " + cell.naturalFeature());
        } else {
            label.setText("Selected " + cell.position() + "  Empty");
        }
    }
}
