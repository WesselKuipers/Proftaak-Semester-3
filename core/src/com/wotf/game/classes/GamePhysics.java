/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author rensphilipsen
 */
public class GamePhysics {
    private Vector2 wind;
    
    public GamePhysics() {
        // TODO: initialize wind
        wind = new Vector2(0, 0);
    }
    
    public Vector2 getWind() {
        return wind;
    }
}
