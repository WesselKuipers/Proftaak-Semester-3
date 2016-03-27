/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.gui.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import java.util.ArrayList;

/**
 *
 * @author Gebruiker
 */
public class SessionLocal implements Screen {

    private List teams;
    private Game game;
    private Stage stage;
    private Skin skin;

    public SessionLocal(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);// Make the stage consume events
        //System.out.println(Gdx.files.internal("maps"));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        // Alle teams en labels hiervoor.
        Table teamstable = new Table();
        Table mapstable = new Table();
        Table settingstable = new Table();

        Label wotflabel = new Label("War of the Figures", skin);
        wotflabel.setPosition(Gdx.graphics.getWidth() / 2 - wotflabel.getWidth() / 2, 740);
        stage.addActor(wotflabel);

        Label iplabel = new Label("IP :", skin);
        settingstable.add(iplabel).width(120);
        Label ipvallabel = new Label("127.0.0.1", skin);
        settingstable.add(ipvallabel).width(120);
        settingstable.row();

        Label gamelabel = new Label("Game :", skin);
        settingstable.add(gamelabel).width(120);
        Label gamevallabel = new Label("Deathmatch", skin);
        settingstable.add(gamevallabel).width(120);
        settingstable.row();

        Label playerslabel = new Label("Players :", skin);
        settingstable.add(playerslabel).width(120);
        Label playersvallabel = new Label("2/10", skin);
        settingstable.add(playersvallabel).width(120);
        settingstable.row();

        Label speedslabel = new Label("Speeds :", skin);
        settingstable.add(speedslabel).width(120);
        Label speedsvallabel = new Label("Speedval", skin);
        settingstable.add(speedsvallabel).width(120);
        settingstable.row();

        Label physicslabel = new Label("Physics :", skin);
        settingstable.add(physicslabel).width(120);
        Label physicsvallabel = new Label("True", skin);
        settingstable.add(physicsvallabel).width(120);
        settingstable.row();

        Label weaponslabel = new Label("Weapons :", skin);
        settingstable.add(weaponslabel).width(120);
        Label weaponsvallabel = new Label("All Weapons", skin);
        settingstable.add(weaponsvallabel).width(120);
        settingstable.row();

        Label timerlabel = new Label("Timer :", skin);
        settingstable.add(timerlabel).width(120);
        Label timervallabel = new Label("1 Hour", skin);
        settingstable.add(timervallabel).width(120);
        settingstable.setWidth(300);
        settingstable.setPosition(280, 210);

        stage.addActor(settingstable);

        String[] teamlist = new String[6];
        teamlist[0] = "Dino  -  Alpha";
        teamlist[1] = "Wessel  -  Beta";
        teamlist[2] = "Lars  -  Gamma";
        teamlist[3] = "Jip  -  Delta";
        teamlist[4] = "Rens  -  Epsi";
        teamlist[5] = "Remco  -  GUI";

        Image map1 = new Image(new Texture("maps/map1.jpg"));
        mapstable.add(map1).width(350).height(200).padBottom(5);
        mapstable.row();
        SelectBox chooseMap = new SelectBox(skin);
        chooseMap.setItems(teamlist);
        mapstable.setPosition(220, 500);
        mapstable.add(chooseMap).width(350);
        stage.addActor(mapstable);

        teams = new List(skin);
        teams.setItems(teamlist);

        Label teamslabel = new Label("Teams", skin);
        teamstable.setPosition(590, 560);
        teamstable.add(teamslabel);
        teamstable.row();
        teamstable.add(teams).width(200);
        teamstable.setWidth(300);
        stage.addActor(teamstable);

        TextButton start = new TextButton("Start", skin); // Use the initialized skin
        start.setColor(Color.BLACK);
        start.setWidth(300);
        start.setHeight(60);
        start.setPosition(590, 250);
        stage.addActor(start);

        TextButton settings = new TextButton("Settings", skin); // Use the initialized skin
        settings.setColor(Color.BLACK);
        settings.setWidth(300);
        settings.setHeight(60);
        settings.setPosition(590, 180);
        stage.addActor(settings);

        TextButton exit = new TextButton("Exit", skin); // Use the initialized skin
        exit.setColor(Color.BLACK);
        exit.setWidth(300);
        exit.setHeight(60);
        exit.setPosition(590, 110);
        stage.addActor(exit);
    }

    @Override
    public void render(float f) {
        Gdx.gl.glClearColor((float) 122 / 255, (float) 122 / 255, (float) 122 / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {
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
