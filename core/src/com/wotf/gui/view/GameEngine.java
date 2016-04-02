/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author DinoS
 */
package com.wotf.gui.view;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.wotf.game.GameStage;
import com.wotf.game.classes.Game;
import com.wotf.game.classes.GameSettings;
import com.wotf.game.classes.Map;
import com.wotf.game.classes.Player;
import com.wotf.game.classes.Team;
import java.util.ArrayList;
import java.util.List;
import com.wotf.game.WotFGame;

public class GameEngine implements Screen {

    private WotFGame game;
    private GameStage stage;
    private List<Team> teams;
    private GameSettings gamesettings;

    public GameEngine(WotFGame game) {
        this.game = game;
    }

    public GameEngine(WotFGame game, GameSettings gamesettings) {
        this.game = game;
        this.gamesettings = gamesettings;
    }

    @Override
    public void show() {
        // Creates default players list and object
        List<Player> players = new ArrayList<>();
        players.add(new Player("127.0.0.1", "DefaultPlayer"));

        // Creates new GameSettings instance
        GameSettings settings = new GameSettings();
        Team teamRed = new Team("Red Team", Color.RED);
        teamRed.addPlayer(players.get(0));
        teamRed.addUnit("Steve", 100);
        teamRed.addUnit("Henk", 100);

        Team teamBlue = new Team("Blue Team", Color.BLUE);
        teamBlue.addPlayer(players.get(0));
        teamBlue.addUnit("Bob", 100);
        teamBlue.addUnit("Appel", 100);
        
        Team teamGreen = new Team("Green Team", Color.GREEN);
        teamGreen.addPlayer(players.get(0));
        teamGreen.addUnit("Groentesoep", 100);
        teamGreen.addUnit("Banaan", 100);
        
        Team teamYellow = new Team("Yellow Team", Color.YELLOW);
        teamYellow.addPlayer(players.get(0));
        teamYellow.addUnit("Appelflap", 100);
        teamYellow.addUnit("Pannekoek", 100);

        settings.addTeam(teamRed);
        settings.addTeam(teamBlue);
        settings.addTeam(teamGreen);
        settings.addTeam(teamYellow);

        Map map = new Map("tempMap");
        map.setWaterLevel(30);
        map.setWidth(2560);
        map.setHeight(720);

        // Creates a new terrain mask and assigns a flat rectangle as terrain
        boolean[][] terrain = new boolean[map.getWidth()][map.getHeight()];
        for (int x = 100; x < (map.getWidth() - 100); x++) {
            for (int y = 0; y < 80; y++) {
                terrain[x][y] = true;
            }
        }
        
        // Creates a platform on the debug map
        for(int x = (map.getWidth() / 2); x < (map.getWidth() / 2 + 50); x++) {
            for(int y = 80; y < 120; y++) {
                terrain[x][y] = true;
            }
        }

        map.setTerrain(terrain);

        // Initializes a viewport and a camera object
        ScreenViewport viewport = new ScreenViewport(new OrthographicCamera(1280, 720));
        viewport.setWorldSize(2560, 720);
        viewport.getCamera().position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        viewport.apply();

        // Initializes game object using game settings
        Game gameclass = new Game(gamesettings, map, players);

        // Initializes the stage object and sets the viewport
        stage = new GameStage(gameclass);
        stage.init();
        stage.setViewport(viewport);

        stage.setKeyboardFocus(stage.getActors().first());

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
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
    public void dispose() {
    }
}