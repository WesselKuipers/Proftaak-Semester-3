package com.wotf.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wotf.gui.view.GameEngine;
import com.wotf.gui.view.MainMenu;

public class WotFGame extends Game {

    SpriteBatch batch;
    Texture img;
    Game game;

    public WotFGame() {
        game = this;
    }

    @Override
    public void create() {
        //setScreen(new MainMenu(game));
        game.setScreen(new GameEngine(game));
    }

}
