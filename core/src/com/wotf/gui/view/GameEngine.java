package com.wotf.gui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wotf.game.GameStage;
import com.wotf.game.GuiStage;
import com.wotf.game.classes.Game;
import com.wotf.game.classes.GameSettings;
import com.wotf.game.classes.Map;
import com.wotf.game.classes.Player;
import java.util.ArrayList;
import java.util.List;
import com.wotf.game.WotFGame;

/**
 * Wrapper class that contains the Game object
 */
public class GameEngine implements Screen {

    private final WotFGame game;
    private GameStage stage;
    private GuiStage stageGUI;
    private GameSettings gameSettings;
    private Map map;

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
    * @param map The terrain of the game
    */
    public GameEngine(WotFGame game, GameSettings gameSettings, Map map) {
        this.game = game;
        this.gameSettings = gameSettings;
        this.map = map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        // Creates default players list and object
        List<Player> players = new ArrayList<>();
        players.add(new Player("127.0.0.1", "DefaultPlayer"));

        map.setWaterLevel(30);

        // Initializes a viewport and a camera object
        ScreenViewport viewport = new ScreenViewport(new OrthographicCamera(1280, 720));
        viewport.setWorldSize(map.getWidth(), map.getHeight());
        viewport.getCamera().position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        viewport.apply();

        // Initializes game object using game settings
        Game gameclass = new Game(gameSettings, map, players);

        stageGUI = new GuiStage(gameclass);
        
        // Initializes the stage object and sets the viewport
        stage = new GameStage(gameclass, stageGUI);
        stage.init();
        stage.setViewport(viewport);
        
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stageGUI);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
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

        stageGUI.act();
        stageGUI.draw();
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