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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.wotf.game.WotFGame;

/**
 * Screen that shows the main menu
 */
public class MainMenu implements Screen {

    private Skin skin;
    private Stage stage;
    private final WotFGame game;

    /**
     * Creates a new instance of the MainMenu based on the game.
     *
     * @param game so we can switch the screen of the 'current' game
     */
    public MainMenu(WotFGame game) {
        this.game = game;
    }

    /**
     * Create a NinePatch Texture based on the internal file name.
     *
     * @param fname the location/name of the internal file
     * @return NinePatch texture with the given bounds which is an internal
     * file.
     */
    private NinePatch getNinePatch(String fname) {

        // Get the image
        final Texture t = new Texture(Gdx.files.internal(fname));

        // create a new texture region, otherwise black pixels will show up too, we are simply cropping the image
        // last 4 numbers respresent the length of how much each corner can draw,
        // for example if your image is 50px and you set the numbers 50, your whole image will be drawn in each corner
        // so what number should be good?, well a little less than half would be nice
        return new NinePatch(new TextureRegion(t, 1, 1, t.getWidth() - 2, t.getHeight() - 2), 200, 200, 200, 200);
    }

    /**
     * First makes a new stage for the MainMenu screen. Fill in the default skin
     * file which is the uiskin.json. Create the Scene2d objects on the screen
     * with the given position. There is a table around each section. There is a
     * list of players which shows the ping of the players. There is a list of
     * sessions which shows all the sessions There are buttons to create a
     * session, join a session and to exit the LobbyGUI screen. Called when this
     * screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);// Make the stage consume events

        // Pak het JSON bestand uit de assets folder. Hier staan alle skins in.
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Image banner = new Image(new Texture(Gdx.files.internal("banner.png")));
        banner.setPosition(Gdx.graphics.getWidth() / 2 - banner.getWidth() / 2, 335);
        stage.addActor(banner);
        
        Label wotflabel = new Label("War of the Figures", skin);
        wotflabel.setPosition(Gdx.graphics.getWidth() / 2 - wotflabel.getWidth() / 2, 480);
        stage.addActor(wotflabel);

        TextButton local = new TextButton("Local", skin); // Use the initialized skin
        local.setColor(Color.BLACK);
        local.setWidth(300);
        local.setHeight(60);
        local.setPosition(Gdx.graphics.getWidth() / 2 - local.getWidth() / 2, 240);
        local.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SessionLocal(game));
            }
        });
        stage.addActor(local);

        TextButton online = new TextButton("Online", skin); // Use the initialized skin
        online.setColor(Color.BLACK);
        online.setWidth(300);
        online.setHeight(60);
        online.setPosition(Gdx.graphics.getWidth() / 2 - online.getWidth() / 2, 170);
        online.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new Username(game));
            }
        });
        stage.addActor(online);

        TextButton about = new TextButton("About", skin); // Use the initialized skin
        about.setColor(Color.BLACK);
        about.setWidth(300);
        about.setHeight(60);
        about.setPosition(Gdx.graphics.getWidth() / 2 - about.getWidth() / 2, 100);
        stage.addActor(about);

        TextButton exit = new TextButton("Exit", skin); // Use the initialized skin
        exit.setColor(Color.BLACK);
        exit.setWidth(300);
        exit.setHeight(60);
        exit.setPosition(Gdx.graphics.getWidth() / 2 - exit.getWidth() / 2, 30);
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        stage.addActor(exit);
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time since the last render.
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
    public void dispose() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void hide() {
    }
}
