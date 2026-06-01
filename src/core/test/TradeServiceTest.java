package core;

import core.app.SnapshotMapper;
import core.app.TradeRequest;
import core.building.BuildingType;
import core.resource.ResourceType;
import core.service.TradeService;
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
