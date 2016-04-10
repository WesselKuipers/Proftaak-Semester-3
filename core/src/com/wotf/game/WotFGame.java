package com.wotf.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wotf.gui.view.MainMenu;

/**
 * ApplicationListener that delegates to a Screen
 * allowing the application to easily have multiple screens
 */
public class WotFGame extends Game {

    SpriteBatch batch;
    Texture img;
    WotFGame game;

    /**
     * Constructor used to assign internal game field
     */
    public WotFGame() {
        game = this;
    }

    /**
     * Called when application is first created
     */
    @Override
    public void create() {
        game.setScreen(new MainMenu(game));
    }
}
