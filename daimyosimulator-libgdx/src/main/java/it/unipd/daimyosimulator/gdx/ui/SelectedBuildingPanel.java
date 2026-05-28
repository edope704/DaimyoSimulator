package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

import java.util.function.Consumer;

public final class SelectedBuildingPanel extends Table {
    private final Skin skin;
    private final GameAssetManager assetManager;
    private final Image iconImage;
    private final Label nameLabel;
    private final Label detailLabel;
    private final TextButton marketButton;
    private CellViewModel currentCell;
    private final Consumer<CellViewModel> onMarketOpen;

    public SelectedBuildingPanel(Skin skin, GameAssetManager assetManager, Consumer<CellViewModel> onMarketOpen) {
        this.skin = skin;
        this.assetManager = assetManager;
        this.onMarketOpen = onMarketOpen;

        setBackground(skin.getDrawable("hud-panel"));
        pad(6);

        iconImage  = new Image();
        nameLabel  = new Label("No tile selected", skin, "title");
        detailLabel = new Label("", skin, "dim");
        detailLabel.setColor(new Color(0.75f, 0.70f, 0.55f, 1f));

        marketButton = new TextButton("Open Market", skin);
        marketButton.setVisible(false);
        marketButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                onMarketOpen.accept(currentCell);
            }
        });

        // Left: icon; Right: name + detail on two rows
        Table infoCol = new Table();
        infoCol.defaults().left();
        infoCol.add(nameLabel).left().padBottom(2);
        infoCol.row();
        infoCol.add(detailLabel).left();

        add(iconImage).size(42, 42).padRight(8).left();
        add(infoCol).expandX().left();
        add(marketButton).padLeft(6).right();
    }

    public void refresh(CellViewModel cell, VillageSnapshot snapshot) {
        currentCell = cell;
        if (cell == null) {
            iconImage.setDrawable(null);
            nameLabel.setText("No tile selected");
            detailLabel.setText("");
            marketButton.setVisible(false);
        } else if (cell.building() != null) {
            iconImage.setDrawable(new TextureRegionDrawable(
                    assetManager.getBuilding(cell.building().type())));
            nameLabel.setText(cell.building().displayName());
            int totalJobs = cell.building().jobSlots().values().stream()
                    .mapToInt(Integer::intValue).sum();
            if (cell.building().type() == BuildingType.RICE_PADDY) {
                boolean active = hasNearbyFarm(snapshot, cell.position().x(), cell.position().y());
                String status = active ? "Status: Active" : "Status: Inactive / No Farm";
                detailLabel.setText(status + "  @(" + cell.position().x() + ", " + cell.position().y() + ")");
            } else {
                String detail = totalJobs > 0
                        ? "Jobs: " + totalJobs + "   (" + cell.position().x() + ", " + cell.position().y() + ")"
                        : "(" + cell.position().x() + ", " + cell.position().y() + ")";
                detailLabel.setText(detail);
            }
            marketButton.setVisible(cell.building().type() == BuildingType.MARKET);
        } else if (cell.naturalFeature() != null) {
            iconImage.setDrawable(null);
            nameLabel.setText(cell.naturalFeature().name().charAt(0)
                    + cell.naturalFeature().name().substring(1).toLowerCase().replace('_', ' '));
            detailLabel.setText("@(" + cell.position().x() + ", " + cell.position().y() + ")");
            marketButton.setVisible(false);
        } else {
            iconImage.setDrawable(null);
            nameLabel.setText("Empty tile");
            detailLabel.setText("@(" + cell.position().x() + ", " + cell.position().y() + ")");
            marketButton.setVisible(false);
        }
    }

    private boolean hasNearbyFarm(VillageSnapshot snapshot, int px, int py) {
        if (snapshot == null) return false;
        return snapshot.cells().stream().anyMatch(c ->
                c.building() != null
                && c.building().type() == BuildingType.RICE_FARM
                && Math.abs(c.position().x() - px) <= 1
                && Math.abs(c.position().y() - py) <= 1);
    }
}
