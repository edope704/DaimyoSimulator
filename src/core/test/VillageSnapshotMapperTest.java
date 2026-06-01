package core;

import core.app.SnapshotMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VillageSnapshotMapperTest {
    @Test
    void snapshotContainsGridAndResources() {
        var snapshot = new SnapshotMapper().toSnapshot(TestFixtures.village());
        assertEquals(5, snapshot.width());
        assertEquals(100, snapshot.resources().rice());
    }
}
