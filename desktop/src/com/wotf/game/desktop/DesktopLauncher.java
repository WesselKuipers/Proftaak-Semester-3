package com.wotf.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.wotf.game.WotFGame;

/**
 *
 * @author Remco
 */
public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
<<<<<<< HEAD
                config.height = 800;
                config.width = 900;
=======
                config.width = 1280;
                config.height = 720;
>>>>>>> refs/remotes/origin/master
		new LwjglApplication(new WotFGame(), config);
	}
}
