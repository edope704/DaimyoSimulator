package it.unipd.daimyosimulator.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import it.unipd.daimyosimulator.gdx.assets.GameAssetManager;

public final class SpeedControlPanel extends Table {
    private boolean paused = false;
    private int speedMultiplier = 1;
    private final TextButton pauseButton;
    private final TextButton speedButton;
    private final ProgressBar tickBar;
    private final Label tickOverlay;
    private final Image pauseIcon;
    private final GameAssetManager assetManager;

    public SpeedControlPanel(Skin skin, GameAssetManager assetManager, Runnable nextTick) {
        this.assetManager = assetManager;
        setBackground(skin.getDrawable("hud-panel"));

        TextButton nextButton = new TextButton("Next", skin);
        nextButton.add(new Image(assetManager.getUi(assetManager.ui().fastButton()))).size(20);

        pauseButton = new TextButton("Pause", skin);
        pauseIcon = new Image(assetManager.getUi(assetManager.ui().pauseButton()));
        pauseButton.add(pauseIcon).size(20);

        speedButton = new TextButton("1x", skin);
        speedButton.add(new Image(assetManager.getUi(assetManager.ui().fastButton()))).size(20);

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
                pauseButton.setText(paused ? "Resume" : "Pause");
                pauseIcon.setDrawable(new TextureRegionDrawable(
                        assetManager.getUi(paused
                                ? assetManager.ui().playButton()
                                : assetManager.ui().pauseButton())));
            }
        });
        speedButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                speedMultiplier = speedMultiplier == 1 ? 2 : speedMultiplier == 2 ? 4 : 1;
                speedButton.setText(speedMultiplier + "x");
            }
        });

        tickBar = new ProgressBar(0, 1, 0.001f, false, skin, "tick-bar-horizontal");
        tickOverlay = new Label("Tick 0", skin, "dim");

        Table labelWrapper = new Table();
        labelWrapper.add(tickOverlay).center();

        Stack tickStack = new Stack();
        tickStack.add(tickBar);
        tickStack.add(labelWrapper);

        defaults().pad(2);
        add(nextButton).width(100);
        add(pauseButton).width(100);
        add(speedButton).width(60);
        row();
        add(tickStack).colspan(3).fillX().height(20).padTop(1).padLeft(2).padRight(2);
    }

    public boolean isPaused() {
        return paused;
    }

    public int getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void updateProgress(float fraction) {
        tickBar.setValue(Math.max(0f, Math.min(1f, fraction)));
    }

    public void updateTick(long tick) {
        tickOverlay.setText("Tick " + tick);
    }
}
