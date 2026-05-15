package it.unipd.daimyosimulator.gdx;

import it.unipd.daimyosimulator.core.domain.Position;
import it.unipd.daimyosimulator.gdx.input.ScreenToGridMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScreenToGridMapperTest {
    @Test
    void worldCoordinatesMapToGridCells() {
        assertEquals(new Position(2, 3), new ScreenToGridMapper().gridAt(65, 96, 32));
    }
}
