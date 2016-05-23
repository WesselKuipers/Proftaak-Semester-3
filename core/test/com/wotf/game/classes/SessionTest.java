/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private Player host;

    @Before
    public void initSession() throws RemoteException {
        host = new Player("127.0.0.1", "DinoHost");
        session = new Session(host, "ROOM", true);
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
    public void testgetPlayers() {
        // One unit has been added, RemcoPlayer. Test if the size is 1.
        assertEquals(1, session.getPlayers().size());
    }

    @Test
    public void testaddPlayer() {
        // Add one player to the list of players and test if the player is succesfully added.
        session.addPlayer(new Player("127.0.0.1", "Lars"));
        // Test if the size of the list now is 2.
        assertEquals(2, session.getPlayers().size());
    }

    @Test
    public void testremovePlayer() {
        // Remove the player from the list and check if it now has 0 size.
        session.removePlayer(playerRemco);
        // There already was 1 player, so now there should be 0
        assertEquals(0, session.getPlayers().size());
    }

    @Test
    public void testsetPlayerList() {
        // Add a list of players to the session (probably from the DB).
        ArrayList<Player> players = new ArrayList<>();
        // Make 2 players.
        Player lars = new Player("127.0.0.1", "Lars");
        Player remco = new Player("127.0.1.1", "Remco");
        // Add the 2 players to the list.
        players.add(lars);
        players.add(remco);
        // Now set the player list to the session.
        session.setPlayerList(players);
        // Check if the list has a size of 2.
        assertEquals(2, session.getPlayers().size());
    }

    @Test
    public void testRegistry() {
        try {
            // Test to create a registry.
            session.createNewRegistry();
        } catch (RemoteException ex) {
            fail("The test made it this far, this means an exception was thrown. So it failed to create a registry.");
        }
    }

    /*@Test
    public void testRemoveRegistry() {
        try {
            // The registry is already created in the above function. 
            // Now try to remove it.
            session.createNewRegistry();
            session.removeRegistry();
        } catch (NoSuchObjectException ex) {
            fail("There is no registry created.");
        } catch (RemoteException ex) {
            fail("Failed to create a registry");
        }
    }*/

    @Test
    public void testgetRoomName() {
        // Test if the room name equals the name I have given in before.
        assertEquals("ROOM", session.getRoomName());
        // Test if the small letters won't match.
        assertNotEquals("room", session.getRoomName());
    }

    @Test
    public void testgetandsetId() {
        // First set an id.
        session.setId(22);
        // Now check if the id actually is 22.
        assertEquals(22, session.getId());
    }

}
