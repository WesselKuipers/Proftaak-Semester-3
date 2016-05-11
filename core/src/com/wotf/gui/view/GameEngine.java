package com.wotf.gui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wotf.game.GameStage;
import com.wotf.game.GuiStage;
import com.wotf.game.classes.Game;
import com.wotf.game.classes.GameSettings;
import com.wotf.game.classes.Map;
import com.wotf.game.classes.Player;
import com.wotf.game.classes.Team;
import java.util.ArrayList;
import java.util.List;
import com.wotf.game.WotFGame;
import com.wotf.game.classes.Session;

/**
 * Wrapper class that contains the Game object
 */
public class GameEngine implements Screen {

    private final WotFGame game;
    private GameStage stage;
    private GuiStage stageGUI;
    private Skin skin;
    private List<Team> teams;
    private GameSettings gameSettings;
    private Session session;
    private Map map;
    private Player playingPlayer;

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
     * @param map
    */
    public GameEngine(WotFGame game, GameSettings gameSettings, Map map) {
        this.game = game;
        this.gameSettings = gameSettings;
        this.map = map;
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
    }
    
    /**
    * Constructor of GameEngine
    * @param game Game that will be launched
     * @param session
    */
    public GameEngine(WotFGame game, Session session, Player playingPlayer) {
        this.game = game;
        this.session = session;
        this.gameSettings = session.getGameSettings();
        this.playingPlayer = playingPlayer;
        this.map = new Map(session.getGameSettings().getMapName());
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        // Creates default players list and object
        /*List<Player> players = new ArrayList<>();
        //players.add(new Player("145.93.92.128", "PlayerHost"));
        players.add(new Player("127.0.0.1", "PlayerHost"));
        players.add(new Player("127.0.0.1", "PlayerClient"));
        
        Player playingPlayer = players.get(0);*/

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
        Game gameclass = new Game(gameSettings, map, session.getPlayers(), playingPlayer);

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