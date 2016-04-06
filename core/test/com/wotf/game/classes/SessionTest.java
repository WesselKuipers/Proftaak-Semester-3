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
public class SessionTest {

    private Session session;
    private Player playerRemco;

    @Before
    public void initSession() {
        Player host = new Player("127.0.0.1", "DinoHost");
        session = new Session(host);
        playerRemco = new Player("127.0.0.1", "RemcoPlayer");
        session.addPlayer(playerRemco);
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", session);
    }
    
    @Test
    public void testgetHost() {
        // Test if the hostname equals the expected name.
        assertEquals("DinoHost", session.getHost().getName());
    }
    
    @Test
    public void testgetPlayers(){
        // One unit has been added, RemcoPlayer. Test if the size is 1.
        assertEquals(1, session.getPlayers().size());
    }
    
    @Test
    public void testaddPlayer(){
        // Add one player to the list of players and test if the player is succesfully added.
        session.addPlayer(new Player("127.0.0.1", "Lars"));
        // Test if the size of the list now is 2.
        assertEquals(2, session.getPlayers().size());
    }
    
    @Test
    public void testremovePlayer(){
        // Remove the player from the list and check if it now has 0 size.
        session.removePlayer(playerRemco);
        // There already was 1 player, so now there should be 0
        assertEquals(0, session.getPlayers().size());
    }
}
