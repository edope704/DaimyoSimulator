package gdx.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import gdx.assets.AudioSettings;
import gdx.assets.GameSoundManager;

/**
 * Modal audio-settings dialog containing two volume sliders.
 * Pass a live {@link GameSoundManager} to apply changes to in-game audio
 * immediately; pass {@code null} when opened from the main menu (changes
 * are persisted in {@link AudioSettings} and picked up when the game starts).
 */
public final class AudioSettingsDialog extends Dialog {

    public AudioSettingsDialog(Skin skin, GameSoundManager soundManager) {
        super("Audio Settings", skin);

        Table content = getContentTable();
        content.pad(20, 24, 12, 24);
        content.defaults().pad(6);

        // ── Music Volume ──────────────────────────────────────────────────────
        content.add(new Label("Music Volume", skin)).left().width(130);

        Slider musicSlider = new Slider(0f, 1f, 0.05f, false, skin);
        musicSlider.setValue(AudioSettings.getMusicVolume());
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float v = musicSlider.getValue();
                AudioSettings.setMusicVolume(v);
                if (soundManager != null) soundManager.setMusicVolume(v);
            }
        });
        content.add(musicSlider).width(240).height(20);
        content.row();

        // ── SFX Volume ────────────────────────────────────────────────────────
        content.add(new Label("SFX Volume", skin)).left().width(130);

        Slider sfxSlider = new Slider(0f, 1f, 0.05f, false, skin);
        sfxSlider.setValue(AudioSettings.getSfxVolume());
        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioSettings.setSfxVolume(sfxSlider.getValue());
            }
        });
        content.add(sfxSlider).width(240).height(20);

        button("Close");
        setMovable(true);
    }
}
