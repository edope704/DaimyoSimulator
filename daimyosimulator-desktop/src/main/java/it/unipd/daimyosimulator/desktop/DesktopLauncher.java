package it.unipd.daimyosimulator.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import it.unipd.daimyosimulator.gdx.DaimyoSimulatorGame;

public final class DesktopLauncher {
    private DesktopLauncher() {
    }

    public static void main(String[] args) {
        org.lwjgl.system.Configuration.GLFW_CHECK_THREAD0.set(false);        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("DaimyoSimulator");
        config.setWindowedMode(1280, 720);
        config.useVsync(true);
        new Lwjgl3Application(new DaimyoSimulatorGame(), config);
    }
}
