/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Remco
 */
public class LobbyTest {

    private Lobby lobby;
    private Player p;
    private Player w;
    private Session s;
    private Session ses;

    @Before
    public void initLobby() throws RemoteException {
        lobby = new Lobby();

        // Add the player
        p = new Player("127.0.0.1", "Playeryo");
        // Add the session
        s = new Session(p, "room");
        // Add another player
        w = new Player("111.21.0.1", "HansDino");
        // Add another session
        ses = new Session(w, "room2");
        
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", lobby);
    }

    @Test
    public void testgetServerIP() throws UnknownHostException{
        // Test the ServerIP. 
        // Check if the IP of the localhost is the same as the set IP for the lobby.
        assertEquals(InetAddress.getLocalHost().toString(), lobby.getServerIp());
    }
    
    @Test
    public void testAddSession() throws RemoteException {
        // Add the session to the lobby.
        lobby.addSession(s);
        // Count the size of the sessions
        assertEquals(1, lobby.getSessions().size());
    }

    @Test
    public void testgetSession() throws RemoteException {
        // Add the session to the lobby.
        lobby.addSession(s);
        // Check if the session equals the given IP's session.
        assertEquals(s, lobby.getSession("127.0.0.1"));
        // Remove the session.
        lobby.removeSession(s);
        // test if it is removed succesfully.
        assertEquals(0, lobby.getSessions().size());
    }

    @Test
    public void removeAllSession() {
        // Add the session to the lobby.
        lobby.addSession(s);
        lobby.addSession(ses);
        
        // Remove all sessions. It should be 0 now.
        lobby.removeAllSessions();
        // Check if it is 0 now.
        assertEquals(0, lobby.getSessions().size());
    }

}
