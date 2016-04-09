package com.wotf.game;

import com.badlogic.gdx.Game;
import com.wotf.game.WotFGame;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wotf.gui.view.MainMenu;

public class WotFGame extends Game {

    SpriteBatch batch;
    Texture img;
    WotFGame game;

    public WotFGame() {
        game = this;
    }

    @Override
    public void create() {
        //setScreen(new MainMenu(game));
        game.setScreen(new MainMenu(game));
    }
    
}
