package it.unipd.daimyosimulator.gdx;

import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.input.ScreenToGridMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScreenToGridMapperTest {
    @Test
    void worldCoordinatesMapToGridCells() {
        // Grid cell (2,3) is at render cell (7,8) with PLAYABLE_OFFSET=5; world coords = 7*32+1, 8*32+1
        assertEquals(new Position(2, 3), new ScreenToGridMapper().gridAt(225, 257, 32));
    }
}
