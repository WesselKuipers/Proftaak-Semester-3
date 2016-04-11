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
    public void testgetMaxTurns() {
        // The default max turns are set to 30 check if this is true.
        assertEquals(30, dsettings.getMaxTurns());
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
    public void testgetMaxTime() {
        // The default max time is set to 1800 check if this is true.
        assertEquals(1800, dsettings.getMaxTime());
    }

    @Test
    public void testsetMaxTime() {
        // The default max time is set to 1800.
        // I am setting the maxTurns to 2200 now.
        dsettings.setMaxTime(2200);

        // Test if the maxTurns is 50 now.
        assertEquals(2200, dsettings.getMaxTime());
    }

    @Test
    public void testgetTurnTime() {
        // The default turn time is set to 30 check if this is true.
        assertEquals(30, dsettings.getTurnTime());
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
}
