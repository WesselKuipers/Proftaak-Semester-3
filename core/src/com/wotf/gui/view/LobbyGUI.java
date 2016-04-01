/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.gui.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.wotf.game.WotFGame;
import com.wotf.game.classes.Lobby;
import com.wotf.game.classes.Session;
import java.util.ArrayList;

/**
 *
 * @author Gebruiker
 */
public class LobbyGUI implements Screen {

    private WotFGame game;
    private Stage stage;
    private Skin skin;
    private List sessions;
    private Pixmap pm1;
    private Lobby lobby;

    public LobbyGUI(WotFGame game) {
        this.game = game;
        lobby = new Lobby();
    }

    private NinePatch getNinePatch(String fname) {
        // Get the image
        final Texture t = new Texture(Gdx.files.internal(fname));

        // create a new texture region, otherwise black pixels will show up too, we are simply cropping the image
        // last 4 numbers respresent the length of how much each corner can draw,
        // for example if your image is 50px and you set the numbers 50, your whole image will be drawn in each corner
        // so what number should be good?, well a little less than half would be nice
        return new NinePatch(new TextureRegion(t, 1, 1, t.getWidth() - 2, t.getHeight() - 2), 150, 150, 200, 200);
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);// Make the stage consume events
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        sessions = new List(skin);
        List players = new List(skin);
        Table sessionstable = new Table();
        Table playerstable = new Table();
        
        sessionstable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"))));
        playerstable.setBackground(new NinePatchDrawable(getNinePatch(("GUI/tblbg.png"))));
        
        String[] playerlist = new String[4];
        playerlist[0] = "Wessel";
        playerlist[1] = "Dino";
        playerlist[2] = "Lars";
        playerlist[3] = "Rens";

        Label wotflabel = new Label("War of the Figures", skin);
        wotflabel.setPosition(Gdx.graphics.getWidth() / 2 - wotflabel.getWidth() / 2, 740);
        stage.addActor(wotflabel);

        Label sessionslabel = new Label("Sessions", skin);
        sessionstable.add(sessionslabel).padRight(20);
        sessionstable.row();
        sessions.setItems(lobby.getSessions());
        sessionstable.add(sessions).minWidth(250).height(200);
        sessionstable.setPosition(30, 260);
        sessionstable.setWidth(300);
        sessionstable.setHeight(400);
        stage.addActor(sessionstable);

        Label playerslabel = new Label("Players", skin);
        playerstable.add(playerslabel).padRight(20);
        playerstable.row();
        players.setItems(playerlist);
        playerstable.add(players).minWidth(250).height(200);
        playerstable.setPosition(500, 260);
        playerstable.setWidth(300);
        playerstable.setHeight(400);
        stage.addActor(playerstable);

        TextButton exit = new TextButton("Exit", skin); // Use the initialized skin
        exit.setColor(Color.BLACK);
        exit.setWidth(300);
        exit.setHeight(60);
        exit.setPosition(950, 30);
        stage.addActor(exit);
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenu(game));
            }
        });

        TextButton join = new TextButton("Join", skin); // Use the initialized skin
        join.setColor(Color.BLACK);
        join.setWidth(300);
        join.setHeight(60);
        join.setPosition(30, 30);
        stage.addActor(join);

        TextButton makesession = new TextButton("Make Session", skin); // Use the initialized skin
        makesession.setColor(Color.BLACK);
        makesession.setWidth(300);
        makesession.setHeight(60);
        makesession.setPosition(350, 30);
        stage.addActor(makesession);

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
