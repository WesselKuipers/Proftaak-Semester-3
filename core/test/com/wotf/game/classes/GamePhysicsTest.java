/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Remco
 */
public class GamePhysicsTest {

    private GamePhysics gamephysics;

    @Before
    public void init() {
        gamephysics = new GamePhysics();
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", gamephysics);
    }

    @Test
    public void testgetWind() {
        // Test if the wind is set to the default vector2(0,0).
        // Make a default vector which is the same as the default constructor variable.
        Vector2 defaultvector = new Vector2(0,0);
        // The vector of the wind. With the default constructor.
        Vector2 wind = gamephysics.getWind();
        // Test if these vectors have the same values.
        assertEquals(defaultvector, wind);
        
    }
}
