package gdx.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import core.app.view.VillageSnapshot;

public final class AnimationRenderer {
    public void render(SpriteBatch batch, VillageSnapshot snapshot, float delta) {
        // Extension point for frame animations; current implementation keeps deterministic static sprites.
    }
}
