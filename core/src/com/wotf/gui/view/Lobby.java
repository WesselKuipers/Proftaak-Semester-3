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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 *
 * @author Gebruiker
 */
public class Lobby implements Screen {

    private Game game;
    private Stage stage;
    private Skin skin;
    private List sessions;

    public Lobby(Game game) {
        this.game = game;
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

        String[] sessionlist = new String[4];
        sessionlist[0] = "Figure_war" + "\t" + "\t" + "Deathmatch";
        sessionlist[1] = "Figures battle" + "\t" + "\t" + "Deathmatch";
        sessionlist[2] = "Actoblo" + "\t" + "\t" + "Deathmatch";
        sessionlist[3] = "FletcherHome" + "\t" + "\t" + "Deathmatch";

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
        sessions.setItems(sessionlist);
        sessionstable.add(sessions).minWidth(250).height(200);
        sessionstable.setPosition(30, 460);
        sessionstable.setWidth(300);
        stage.addActor(sessionstable);

        Label playerslabel = new Label("Players", skin);
        playerstable.add(playerslabel).padRight(20);
        playerstable.row();
        players.setItems(playerlist);
        playerstable.add(players).minWidth(250).height(200);
        playerstable.setPosition(500, 460);
        playerstable.setWidth(300);
        stage.addActor(playerstable);
        
        TextButton exit = new TextButton("Exit", skin); // Use the initialized skin
        exit.setColor(Color.BLACK);
        exit.setWidth(300);
        exit.setHeight(60);
        exit.setPosition(30, 650);
        stage.addActor(exit);

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
