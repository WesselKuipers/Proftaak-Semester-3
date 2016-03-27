<<<<<<< HEAD
package com.wotf.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wotf.gui.view.MainMenu;

public class WotFGame extends Game {
	SpriteBatch batch;
	Texture img;
        Game game;
        
        public WotFGame()
        {
            game = this;
        }
	
	@Override
	public void create () {
                setScreen(new MainMenu(game));
	}

}
=======
package com.wotf.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.wotf.game.classes.Game;
import com.wotf.game.classes.GameSettings;
import com.wotf.game.classes.Map;
import com.wotf.game.classes.Player;
import com.wotf.game.classes.Team;
import java.util.ArrayList;
import java.util.List;

public class WotFGame extends ApplicationAdapter {

    GameStage stage;

    @Override
    public void create() {
        // Creates default players list and object
        List<Player> players = new ArrayList<>();
        players.add(new Player("127.0.0.1", "DefaultPlayer"));

        // Creates new GameSettings instance
        GameSettings settings = new GameSettings();
        Team teamRed = new Team("Red Team", Color.RED);
        teamRed.addPlayer(players.get(0));
        teamRed.addUnit("Steve", 100);
        teamRed.addUnit("Henk", 1000000);

        Team teamBlue = new Team("Blue Team", Color.BLUE);
        teamBlue.addPlayer(players.get(0));
        teamBlue.addUnit("Bob", 1000);

        settings.addTeam(teamRed);
        settings.addTeam(teamBlue);
        
        Map map = new Map("tempMap");
        map.setWaterLevel(30);
        map.setWidth(2560);
        map.setHeight(720);
        
        // Creates a new terrain mask and assigns a flat rectangle as terrain
        boolean[][] terrain = new boolean[map.getWidth()][map.getHeight()];
        for(int x = 100; x < map.getWidth() - 100; x++) {
            for(int y = 0; y < 80; y++) {
                terrain[x][y] = true;
            }
        }
        
        map.setTerrain(terrain);
        
        // Initializes a viewport and a camera object
        StretchViewport viewport = new StretchViewport(2560, 720);
        viewport.setCamera(new OrthographicCamera(1280, 720));
        viewport.getCamera().position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);

        // Initializes game object using game settings
        Game game = new Game(settings, map, players);

        // Initializes the stage object and sets the viewport
        stage = new GameStage(game);
        stage.init();
        stage.setViewport(viewport);

        stage.setKeyboardFocus(stage.getActors().first());
        
        Gdx.input.setInputProcessor(stage);

        // Debug function: If tab is pressed, toggle between actor 1 and actor 2
        stage.addListener(new InputListener() {
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.TAB) {
                    if (stage.getKeyboardFocus() == stage.getActors().get(0)) {
                        stage.setKeyboardFocus(stage.getActors().get(1));
                    } else {
                        stage.setKeyboardFocus(stage.getActors().get(0));
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
}
>>>>>>> refs/remotes/origin/master
