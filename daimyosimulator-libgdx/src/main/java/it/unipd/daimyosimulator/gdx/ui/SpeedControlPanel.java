package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

public final class SpeedControlPanel extends Table {
    private boolean paused = true;
    private int speedMultiplier = 1;
    private final TextButton pauseButton;
    private final TextButton speedButton;

    public SpeedControlPanel(Skin skin, GameAssetManager assetManager, Runnable nextTick) {
        setBackground(skin.getDrawable("hud-panel"));
        TextButton nextButton = new TextButton("Next", skin);
        nextButton.add(new com.badlogic.gdx.scenes.scene2d.ui.Image(assetManager.getUi(assetManager.ui().playButton()))).size(24);
        pauseButton = new TextButton("Pause", skin);
        pauseButton.add(new com.badlogic.gdx.scenes.scene2d.ui.Image(assetManager.getUi(assetManager.ui().pauseButton()))).size(24);
        speedButton = new TextButton("1x", skin);
        speedButton.add(new com.badlogic.gdx.scenes.scene2d.ui.Image(assetManager.getUi(assetManager.ui().fastButton()))).size(24);
        nextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                nextTick.run();
            }
        });
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                paused = !paused;
                pauseButton.setText(paused ? "Paused" : "Running");
            }
        });
        speedButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                speedMultiplier = speedMultiplier == 1 ? 2 : speedMultiplier == 2 ? 4 : 1;
                speedButton.setText(speedMultiplier + "x");
            }
        });
        defaults().pad(2);
        add(nextButton).width(100);
        add(pauseButton).width(90);
        add(speedButton).width(60);
    }

    public boolean isPaused() {
        return paused;
    }

    public int getSpeedMultiplier() {
        return speedMultiplier;
    }
}
