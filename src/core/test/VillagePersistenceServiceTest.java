package core;

import core.persistence.VillageMapper;
import core.persistence.VillagePersistenceService;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

class VillagePersistenceServiceTest {
    @Test
    void saveWritesJsonFile() throws Exception {
        var path = Files.createTempFile("daimyo", ".json");
        new VillagePersistenceService(new VillageMapper()).save(TestFixtures.village(), path);
        assertTrue(Files.size(path) > 0);
        Files.deleteIfExists(path);
    }
}
