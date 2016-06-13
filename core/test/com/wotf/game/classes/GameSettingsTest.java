/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import HeadlessRunner.GdxTestRunner;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import java.rmi.RemoteException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 *
 * @author Remco
 */
@RunWith(GdxTestRunner.class)
public class GameSettingsTest {

    private GameSettings dsettings;

    @Before
    public void initGameSettings() {
        dsettings = new GameSettings(); 
        
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", dsettings);
    }

    @Test
    public void testgetandsetMapIndex() {
        // Set the map index.
        dsettings.setMapIndex(3);

        // Test if the value of return is 3 now.
        assertEquals(3, dsettings.getMapIndex());
    }

    @Test
    public void testgetandsetMapName() {
        // Set the map name.
        dsettings.setMapName("Wesselmap");

        // Test if the value of return is "Wesselmap" now.
        assertEquals("Wesselmap", dsettings.getMapName());
    }

    @Test
    public void testgetMaxTurns() {
        // The default max turns are set to 30 check if this is true.
        assertEquals(30, dsettings.getMaxTurns());
    }

    @Test
    public void testaddTeam() {
        // Add the teams with 2 different methods. And check if the size will be 2 after.
        Team team = new Team("Betaar", Color.GREEN);
        dsettings.addTeam(team);

        assertEquals(1, dsettings.getTeams().size());
        
        dsettings.addTeam("Snatch", Color.BLACK);
        assertEquals(2, dsettings.getTeams().size());
    }

    @Test
    public void testremoveTeam() {
        // Add a team to the gamesettings.
        Team team = new Team("Betaar", Color.GREEN);

        dsettings.addTeam(team);
        // Remove the team again to check this method.
        dsettings.removeTeam(team);
        // The size should be 0 again now. 
        assertEquals(0, dsettings.getTeams().size());
    }

    @Test
    public void testsetMaxTurns() {
        // The default max turns are set to 30.
        // I am setting the maxTurns to 50 now.
        dsettings.setMaxTurns(50);

        // Test if the maxTurns is 50 now.
        assertEquals(50, dsettings.getMaxTurns());
    }

    @Test
    public void testgetandsetMaxplayersSession() throws RemoteException {
        // Set the map index.
        dsettings.setMaxPlayersSession(2);

        // Test if the value of return is 2 now.
        assertEquals(2, dsettings.getMaxPlayersSession());
    }

    @Test
    public void testgetMaxTime() {
        // The default max time is set to 3600 check if this is true.
        assertEquals(3600, dsettings.getMaxTime());
    }

    @Test
    public void testsetMaxTime() {
        // The default max time is set to 1800.
        // I am setting the maxTurns to 2200 now.
        dsettings.setMaxTime(2200);

        // Test if the maxTurns is 50 now.
        assertEquals(2200 * 60, dsettings.getMaxTime());
    }

    @Test
    public void testgetandsetMaxUnitCount() {
        // Sets the value to 2.
        dsettings.setMaxUnitCount(2);

        // Gets the value which should be 2 now.
        assertEquals(2, dsettings.getMaxUnitCount());
    }

    @Test
    public void testgetTurnTime() {
        // The default turn time is set to 40 check if this is true.
        assertEquals(40, dsettings.getTurnTime());
    }

    @Test
    public void testsetTurnTime() {
        // The default turn time is set to 30.
        // I am setting the turn time to 55 now.
        dsettings.setTurnTime(55);

        // Test if the turn time is 55 now.
        assertEquals(55, dsettings.getTurnTime());
    }

    @Test
    public void testgetWithdrawTime() {
        // The default withdraw time is set to 3 check if this is true.
        assertEquals(3, dsettings.getWithdrawTime());
    }

    @Test
    public void testsetWithdrawTime() {
        // The default withdraw time is set to 3.
        // I am setting the withdraw time to 6 now.
        dsettings.setWithdrawTime(6);

        // Test if the withdraw time is 55 now.
        assertEquals(6, dsettings.getWithdrawTime());
    }

    @Test
    public void testgetFallingDamage() {
        // The default falling damage is set to true check if this is true.
        assertTrue(dsettings.isFallingDamage());
    }

    @Test
    public void testsetFallingDamage() {
        // The default falling damage is set to true check if this is true.
        // I am setting the falling damage to false now.
        dsettings.setFallingDamage(false);

        // Test if the falling damage is false now.
        assertEquals(false, dsettings.isFallingDamage());
    }

    @Test
    public void testgetSuddenDeath() {
        // The default sudden death is set to true check if this is true.
        assertEquals(true, dsettings.isSuddenDeath());
    }

    @Test
    public void testsetSuddenDeath() {
        // The default sudden death is set to true check if this is true.
        // I am setting the sudden death to false now.
        dsettings.setSuddenDeath(false);

        // Test if the sudden death is false now.
        assertEquals(false, dsettings.isSuddenDeath());
    }

    @Test
    public void testgetandsetPhysics() {
        // Set the physics to false and check if it is false now.
        // It starts at true.
        dsettings.setPhysics(false);
        // Test if it is False now.
        assertEquals(false, dsettings.getPhysics());
    }
}
