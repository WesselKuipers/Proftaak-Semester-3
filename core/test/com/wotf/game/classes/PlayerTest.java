/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Remco
 */
public class PlayerTest {
    private Player player;
    
    @Before
    public void initGameSettings() {
        player = new Player("127.0.0.1", "Wessel");
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", player);
    }
    
    @Test
    public void testgetIp(){
        // The IP has been set to 127.0.0.1 so with the get method we should be able to get the same result.
        assertEquals("127.0.0.1", player.getIp());
    }
    
    @Test
    public void testgetName() {
        // The name has been set to Wessel so with the get method we should be able to get the same result.
        assertEquals("Wessel", player.getName());
    }
    
    @Test
    public void testsetName() { 
        // The current set name is Wessel.
        // Now we're going to set a new name: Rens
        player.setName("Rens");
        // Test if the name is set correctly.
        assertEquals("Rens", player.getName());
    }
}
