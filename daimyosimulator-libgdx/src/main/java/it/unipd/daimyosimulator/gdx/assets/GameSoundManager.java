package it.unipd.daimyosimulator.gdx.assets;

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
        bgMusic.setVolume(0.5f);
        bgMusic.play();

        clickSound    = Gdx.audio.newSound(Gdx.files.internal(AUDIO_DIR + "sfx_click.mp3"));
        buildSound    = Gdx.audio.newSound(Gdx.files.internal(AUDIO_DIR + "sfx_build.mp3"));
        demolishSound = Gdx.audio.newSound(Gdx.files.internal(AUDIO_DIR + "sfx_demolish.mp3"));
    }

    public void playClick()    { clickSound.play(0.8f); }
    public void playBuild()    { buildSound.play(1.0f); }
    public void playDemolish() { demolishSound.play(1.0f); }

    @Override
    public void dispose() {
        bgMusic.dispose();
        clickSound.dispose();
        buildSound.dispose();
        demolishSound.dispose();
    }
}
