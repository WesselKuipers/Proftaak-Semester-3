package com.wotf.gui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wotf.game.GameStage;
import com.wotf.game.classes.Game;
import com.wotf.game.classes.GameSettings;
import com.wotf.game.classes.Map;
import com.wotf.game.classes.Player;
import com.wotf.game.classes.Team;
import java.util.ArrayList;
import java.util.List;
import com.wotf.game.WotFGame;

/**
 * Wrapper class that contains the Game object
 */
public class GameEngine implements Screen {

    private final WotFGame game;
    private GameStage stage;
    private List<Team> teams;
    private GameSettings gameSettings;

    /**
     * Constructor of GameEngine
     * @param game Game that will be launched
     */
    public GameEngine(WotFGame game) {
        this.game = game;
    }

    /**
    * Constructor of GameEngine
    * @param game Game that will be launched
    * @param gameSettings Settings associated with this game
    */
    public GameEngine(WotFGame game, GameSettings gameSettings) {
        this.game = game;
        this.gameSettings = gameSettings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        // Creates default players list and object
        List<Player> players = new ArrayList<>();
        players.add(new Player("127.0.0.1", "DefaultPlayer"));

        Map map = new Map("maps/STONES.PNG");
        map.setWaterLevel(30);
        
        /* Debug map:
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
        */
        
        // Initializes a viewport and a camera object
        ScreenViewport viewport = new ScreenViewport(new OrthographicCamera(1280, 720));
        viewport.setWorldSize(map.getWidth(), map.getHeight());
        viewport.getCamera().position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        viewport.apply();

        // Initializes game object using game settings
        Game gameclass = new Game(gameSettings, map, players);

        // Initializes the stage object and sets the viewport
        stage = new GameStage(gameclass);
        stage.init();
        stage.setViewport(viewport);

        Gdx.input.setInputProcessor(stage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hide() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resize(int width, int height) {
        // Passes the new width and height to the viewport
        stage.getViewport().update(width, height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pause() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resume() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
    }
}