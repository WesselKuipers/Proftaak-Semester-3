/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.gui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.wotf.game.WotFGame;
import com.wotf.game.classes.Player;
import com.wotf.game.database.PlayerContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dino Spong
 */
public class Username implements Screen {

    private final WotFGame game;
    private Stage stage;
    private Skin skin;

    public Username(WotFGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);// Make the stage consume events
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextField username = new TextField("djdino56", skin);
        username.setWidth(300);
        username.setHeight(60);
        username.setPosition(500, 400);
        stage.addActor(username);

        TextButton join = new TextButton("Join", skin); // Use the initialized skin
        join.setColor(Color.BLACK);
        join.setWidth(300);
        join.setHeight(60);
        join.setPosition(500, 300);
        stage.addActor(join);

        join.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerContext playerContext = new PlayerContext();
                Player player = null;

                if (username.getText() != null) {
                    try {

                        player = new Player(InetAddress.getLocalHost().getHostAddress(), username.getText());
                        playerContext.insert(player);
                        player = playerContext.getLastAddedPlayer();

                    } catch (SQLException | UnknownHostException ex) {
                        Logger.getLogger(Username.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                try {
                    game.setScreen(new LobbyGUI(game, player));
                } catch (SQLException ex) {
                    Logger.getLogger(Username.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor((float) 122 / 255, (float) 122 / 255, (float) 122 / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    /**
     * Called when the {@link Application} is resized. This can happen at any
     * point during a non-paused state but will never happen before a call to
     * {@link #create()}.
     *
     * @param width the new width in pixels
     * @param height the new height in pixels
     */
    @Override
    public void resize(int width, int height) {
        // Passes the new width and height to the viewport
        stage.getViewport().update(width, height);
    }

    /**
     * Called when the {@link Application} is paused, usually when it's not
     * active or visible on screen. An Application is also paused before it is
     * destroyed.
     */
    @Override
    public void pause() {
    }

    /**
     * Called when the {@link Application} is resumed from a paused state,
     * usually when it regains focus.
     */
    @Override
    public void resume() {
    }

    /**
     * Called when this screen is no longer the current screen for a
     * {@link Game}.
     */
    @Override
    public void hide() {
    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
    }

}
