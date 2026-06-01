package core;

import core.app.GameController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameControllerTest {
    @Test
    void controllerStartsNewVillage() {
        GameController controller = new GameController(TestFixtures.config(), TestFixtures.random());
        assertEquals(4, controller.startNewVillage(4, 4).width());
    }
}
