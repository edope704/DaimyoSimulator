package gdx.adapter;

import core.app.view.CellViewModel;
import core.app.view.VillageSnapshot;
import gdx.assets.BuildingSpriteRegistry;
import gdx.model.BuildingRenderModel;
import gdx.model.CellRenderModel;

import java.util.List;

public final class SnapshotToRenderModelAdapter {
    private final BuildingSpriteRegistry buildingSpriteRegistry;

    public SnapshotToRenderModelAdapter(BuildingSpriteRegistry buildingSpriteRegistry) {
        this.buildingSpriteRegistry = buildingSpriteRegistry;
    }

    public List<CellRenderModel> adapt(VillageSnapshot snapshot) {
        return snapshot.cells().stream().map(this::adaptCell).toList();
    }

    private CellRenderModel adaptCell(CellViewModel cell) {
        BuildingRenderModel building = null;
        if (cell.building() != null) {
            building = new BuildingRenderModel(
                    cell.building().type(),
                    cell.position(),
                    buildingSpriteRegistry.spriteName(cell.building().type())
            );
        }
        return new CellRenderModel(cell.position(), cell.naturalFeature(), building);
    }
}
