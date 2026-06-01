package core;

import core.building.BuildingType;
import core.persistence.VillageMapper;
import core.persistence.VillagePersistenceService;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaveLoadIntegrationTest {
    @Test
    void loadRestoresTickAndBuildings() throws Exception {
        var village = TestFixtures.village();
        TestFixtures.place(village, BuildingType.DWELLING, 0, 0);
        village.setTickNumber(3);
        var path = Files.createTempFile("daimyo-load", ".json");
        VillagePersistenceService service = new VillagePersistenceService(new VillageMapper());
        service.save(village, path);
        var loaded = service.load(path, TestFixtures.config());
        assertEquals(3, loaded.getTickNumber());
        assertEquals(1, loaded.getGrid().countBuildings(BuildingType.DWELLING));
        Files.deleteIfExists(path);
    }
}
