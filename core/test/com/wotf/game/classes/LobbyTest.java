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
public class LobbyTest {

    private Lobby lobby;

    @Before
    public void initLobby() {
        lobby = new Lobby();
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", lobby);
    }
}
