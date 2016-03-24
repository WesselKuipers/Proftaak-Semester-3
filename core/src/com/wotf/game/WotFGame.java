package com.wotf.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.wotf.game.classes.Game;
import com.wotf.game.classes.GameSettings;
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
        Team teamRed = new Team("Red Team", Color.VIOLET);
        teamRed.addPlayer(players.get(0));
        teamRed.addUnit("Steve", 100);
        teamRed.addUnit("Henk", 1000000);

        Team teamBlue = new Team("Blue Team", Color.BLUE);
        teamBlue.addPlayer(players.get(0));
        teamBlue.addUnit("Bob", 1000);

        settings.addTeam(teamRed);
        settings.addTeam(teamBlue);

        // Initializes game object using game settings
        Game game = new Game(settings, players);

        // Initializes the stage object
        stage = new GameStage(game);
        stage.init();

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
