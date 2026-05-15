package it.unipd.daimyosimulator.core.app.view;

public record DashboardViewModel(
        ResourceViewModel resources,
        PopulationViewModel population,
        VillageParametersViewModel parameters,
        PolicyViewModel policy,
        long tick,
        EventLogViewModel eventLog,
        CellViewModel selectedCell
) {
}
