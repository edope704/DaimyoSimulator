package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

import java.util.function.Consumer;

public final class SelectedBuildingPanel extends Table {
    private final Label label;
    private final TextButton marketButton;
    private CellViewModel currentCell;
    private final Consumer<CellViewModel> onMarketOpen;

    public SelectedBuildingPanel(Skin skin, GameAssetManager assetManager, Consumer<CellViewModel> onMarketOpen) {
        this.onMarketOpen = onMarketOpen;
        setBackground(skin.getDrawable("hud-panel"));
        label = new Label("No selection", skin);
        marketButton = new TextButton("Open Market", skin);
        marketButton.setVisible(false);
        marketButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                onMarketOpen.accept(currentCell);
            }
        });
        add(label).left().width(250);
        add(marketButton).padLeft(6);
    }

    public void refresh(CellViewModel cell) {
        currentCell = cell;
        if (cell == null) {
            label.setText("No selection");
            marketButton.setVisible(false);
        } else if (cell.building() != null) {
            boolean isMarket = cell.building().type() == BuildingType.MARKET;
            label.setText(cell.building().displayName() + " @ " + cell.position()
                    + "  Jobs: " + cell.building().jobSlots());
            marketButton.setVisible(isMarket);
        } else if (cell.naturalFeature() != null) {
            label.setText(cell.naturalFeature() + " @ " + cell.position());
            marketButton.setVisible(false);
        } else {
            label.setText("Empty @ " + cell.position());
            marketButton.setVisible(false);
        }
    }
}
