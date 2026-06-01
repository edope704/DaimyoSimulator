package gdx.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

public final class GameSoundManager implements Disposable {
    private static final String AUDIO_DIR = "assets/audio/";

    private final Music  bgMusic;
    private final Sound  clickSound;
    private final Sound  buildSound;
    private final Sound  demolishSound;

    public GameSoundManager() {
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal(AUDIO_DIR + "music_bg.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(AudioSettings.getMusicVolume());
        bgMusic.play();

        clickSound    = Gdx.audio.newSound(Gdx.files.internal(AUDIO_DIR + "sfx_click.mp3"));
        buildSound    = Gdx.audio.newSound(Gdx.files.internal(AUDIO_DIR + "sfx_build.mp3"));
        demolishSound = Gdx.audio.newSound(Gdx.files.internal(AUDIO_DIR + "sfx_demolish.mp3"));
    }

    // ── Playback ──────────────────────────────────────────────────────────────

    public void playClick()    { clickSound.play(AudioSettings.getSfxVolume() * 0.8f); }
    public void playBuild()    { buildSound.play(AudioSettings.getSfxVolume()); }
    public void playDemolish() { demolishSound.play(AudioSettings.getSfxVolume()); }

    // ── Volume control (called by AudioSettingsDialog) ────────────────────────

    /** Updates both AudioSettings and the live background music volume. */
    public void setMusicVolume(float v) {
        AudioSettings.setMusicVolume(v);
        bgMusic.setVolume(AudioSettings.getMusicVolume());
    }

    /** Updates AudioSettings; the new SFX volume is read on the next play call. */
    public void setSfxVolume(float v) {
        AudioSettings.setSfxVolume(v);
    }

    @Override
    public void dispose() {
        bgMusic.dispose();
        clickSound.dispose();
        buildSound.dispose();
        demolishSound.dispose();
    }
}
