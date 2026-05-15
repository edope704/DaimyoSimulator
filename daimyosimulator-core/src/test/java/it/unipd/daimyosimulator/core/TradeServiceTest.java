package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.app.SnapshotMapper;
import it.unipd.daimyosimulator.core.app.TradeRequest;
import it.unipd.daimyosimulator.core.building.BuildingType;
import it.unipd.daimyosimulator.core.resource.ResourceType;
import it.unipd.daimyosimulator.core.service.TradeService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TradeServiceTest {
    @Test
    void marketAllowsImmediateTrade() {
        var village = TestFixtures.village();
        TestFixtures.place(village, BuildingType.MARKET, 0, 0);
        var result = new TradeService(new SnapshotMapper())
                .trade(village, new TradeRequest(ResourceType.TIMBER, ResourceType.RICE, 4));
        assertTrue(result.success());
    }
}
