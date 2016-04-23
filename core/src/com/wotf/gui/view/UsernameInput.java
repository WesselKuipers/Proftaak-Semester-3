/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.gui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.wotf.game.WotFGame;
import com.wotf.game.classes.Player;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author Remco
 */
public class UsernameInput implements Screen {

    private final WotFGame game;
    private Stage stage;
    private Skin skin;
    InetAddress localhost;

    /**
     * Creates a new instance of the MainMenu based on the game.
     *
     * @param game so we can switch the screen of the 'current' game
     */
    public UsernameInput(WotFGame game) throws UnknownHostException {
        this.localhost = InetAddress.getLocalHost();
        this.game = game;
    }
    
    @Override
    public void show() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);// Make the stage consume events

        // Pak het JSON bestand uit de assets folder. Hier staan alle skins in.
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Label wotflabel = new Label("War of the Figures", skin);
        wotflabel.setPosition(Gdx.graphics.getWidth() / 2 - wotflabel.getWidth() / 2, 740);
        stage.addActor(wotflabel);

        TextField tbUsername = new TextField("Fill in your Username", skin);
        tbUsername.setWidth(300);
        tbUsername.setHeight(60);
        tbUsername.setPosition(Gdx.graphics.getWidth() / 2 - tbUsername.getWidth() / 2, 500);
        stage.addActor(tbUsername);
        tbUsername.setText("oki");

        TextButton btSubmit = new TextButton("Submit username", skin);
        btSubmit.setWidth(300);
        btSubmit.setHeight(70);
        btSubmit.setPosition(Gdx.graphics.getWidth() / 2 - btSubmit.getWidth() / 2, 420);
        stage.addActor(btSubmit);
        btSubmit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Player player = new Player(tbUsername.getText(), localhost.getHostAddress());
                game.setScreen(new LobbyGUI(game, player));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor((float) 122 / 255, (float) 122 / 255, (float) 122 / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Passes the new width and height to the viewport
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

}
