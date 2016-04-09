package com.wotf.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.wotf.game.WotFGame;

/**
 * Main entry point of the desktop application
 * @author Remco
 */
public class DesktopLauncher {
    /**
     * Main entry point of the game on desktop machines
     * @param arg Argslist
     */
    public static void main (String[] arg) {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            // Sets the default width and height of the window to 720p
            config.width = 1280;
            config.height = 720;
            LwjglApplication lwjglApplication = new LwjglApplication(new WotFGame(), config);
    }
}
