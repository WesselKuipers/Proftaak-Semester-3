package com.wotf.game.classes;

import com.badlogic.gdx.math.Vector2;

/**
 * Class containing the current variables regarding the physics the game
 * @author rensphilipsen
 */
public class GamePhysics {

    private final Vector2 wind;

    /**
     * Main constructor, initializes the wind Vector2
     */
    public GamePhysics() {
        // TODO: initialize wind
        wind = new Vector2(0, 0);
    }

    /**
     * @return The current wind as Vector2
     */
    public Vector2 getWind() {
        return wind;
    }
}
