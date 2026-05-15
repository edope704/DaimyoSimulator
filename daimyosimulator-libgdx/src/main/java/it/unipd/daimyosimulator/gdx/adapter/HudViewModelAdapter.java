package it.unipd.daimyosimulator.gdx.adapter;

import it.unipd.daimyosimulator.core.app.view.DashboardViewModel;

public final class HudViewModelAdapter {
    public String resourceLine(DashboardViewModel dashboard) {
        return "Rice " + dashboard.resources().rice()
                + " | Timber " + dashboard.resources().timber()
                + " | Tools " + dashboard.resources().tools()
                + " | Luxury " + dashboard.resources().luxuryGoods();
    }
}
