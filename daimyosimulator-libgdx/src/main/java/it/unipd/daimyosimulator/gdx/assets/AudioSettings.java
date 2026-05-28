package it.unipd.daimyosimulator.gdx.assets;

/**
 * Centralized, static storage for audio volume levels.
 * Survives screen transitions; GameSoundManager reads from here on every
 * play call so changes made in the settings dialog take effect immediately.
 */
public final class AudioSettings {

    private static float musicVolume = 0.50f;
    private static float sfxVolume   = 0.80f;

    private AudioSettings() {}

    public static float getMusicVolume() { return musicVolume; }
    public static float getSfxVolume()   { return sfxVolume;   }

    public static void setMusicVolume(float v) { musicVolume = clamp(v); }
    public static void setSfxVolume(float v)   { sfxVolume   = clamp(v); }

    private static float clamp(float v) { return Math.max(0f, Math.min(1f, v)); }
}
