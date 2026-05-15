package it.unipd.daimyosimulator.core;

import it.unipd.daimyosimulator.core.persistence.VillageMapper;
import it.unipd.daimyosimulator.core.persistence.VillagePersistenceService;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertThrows;

class InvalidSaveFileTest {
    @Test
    void corruptedJsonRejected() throws Exception {
        var path = Files.createTempFile("daimyo-bad", ".json");
        Files.writeString(path, "{bad json");
        assertThrows(Exception.class, () -> new VillagePersistenceService(new VillageMapper()).load(path, TestFixtures.config()));
        Files.deleteIfExists(path);
    }
}
