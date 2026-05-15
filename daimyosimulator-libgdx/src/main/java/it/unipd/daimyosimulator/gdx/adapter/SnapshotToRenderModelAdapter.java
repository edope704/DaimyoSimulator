package it.unipd.daimyosimulator.gdx.adapter;

import it.unipd.daimyosimulator.core.app.view.CellViewModel;
import it.unipd.daimyosimulator.core.app.view.VillageSnapshot;
import it.unipd.daimyosimulator.gdx.assets.BuildingSpriteRegistry;
import it.unipd.daimyosimulator.gdx.model.BuildingRenderModel;
import it.unipd.daimyosimulator.gdx.model.CellRenderModel;

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
