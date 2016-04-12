/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Remco
 */
public class TeamTest {

    private Team team;
    private GameSettings gamesettings;
    private Player player;

    @Before
    public void initTeam() {
        /**
         * Constructor of Team, Initialize lists and set active unit index to
         * zero.
         *
         * @param name
         * @param color
         */
        gamesettings = new GameSettings(true);
        player = new Player("127.0.0.1", "Wessel");
        team = new Team("Alpha", Color.BLACK, true);
        team.addPlayer(player);
        team.addUnit("AlphaUnit", 50, new Vector2(40, 80), true);
        team.addUnit("AlphaUnit2", 80, new Vector2(90, 80), true);
        gamesettings.addTeam(team);
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", team);
    }

    @Test
    public void testgetPlayers() {
        /**
         *
         * @return all the players of the team
         */
        // I added one player so the size of this list should be one.
        assertEquals(1, team.getPlayers().size());
    }

    @Test
    public void testaddPlayer() {
        /**
         * Adds a player to team
         *
         * @param p player
         */
        // First add a new player.
        team.addPlayer(new Player("127.0.0.2", "Rens"));
        // Test if the size is 2 now. This means it's added.
        assertEquals(2, team.getPlayers().size());
    }

    @Test
    public void testremovePlayer() {
        /**
         * TODO: Logic for kicking player out Removes a player from the team
         *
         * @param p player
         */
        // Remove the first created player.
        team.removePlayer(player);
        // Test if the size is 0 again now.
        assertEquals(0, team.getPlayers().size());
    }

    @Test
    public void testgetName() {
        /**
         *
         * @return the team name
         */
        assertEquals("Alpha", team.getName());
    }

    @Test
    public void testsetName() {
        /**
         * Set the name of the team
         *
         * @param name
         */
        // Set the name to a new name first.
        team.setName("Beta");
        // Now get the name again to check if it is set.
        assertEquals("Beta", team.getName());
    }

    @Test
    public void testgetColor() {
        /**
         *
         * @return color of team
         */
        assertEquals(Color.BLACK, team.getColor());
    }

    @Test
    public void testsetColor() {
        /**
         * set the color of team
         *
         * @param color color of team
         */
        // First set the color to gray.
        team.setColor(Color.GRAY);
        // Get the newly set color.
        assertEquals(Color.GRAY, team.getColor());
    }

    @Test
    public void testgetUnits() {
        /**
         *
         * @return all the units of team
         */
        // I added one unit so the size should be one.
        assertEquals(2, team.getUnits().size());
    }

    @Test
    public void testgetActiveUnit() {
        /**
         *
         * @return active unit by active unit index
         */
        // The name of the unit which is added to the team.
        String unitname = "AlphaUnit";
        assertEquals(unitname, team.getActiveUnit().getName());
    }

    @Test
    public void testgetUnit() {
        /**
         *
         * @param index index of player
         * @return unit by index
         */
        String unitname = "AlphaUnit";
        assertEquals(unitname, team.getUnit(0).getName());
    }

    @Test
    public void testremoveUnit() {
        /**
         * When unit is killed (health is zero or lower), remove the actor and
         * unit from team
         *
         * @param unit to be removed
         */
        // Get a unit.
        Unit unit = team.getUnit(0);
        // Remove the taken unit.
        team.removeUnit(unit, true);
        // Test if the list size is 0 now.
        assertEquals(1, team.getPlayers().size());
    }

    @Test
    public void testgetActiveUnitIndex() {
        /**
         *
         * @return active unit index
         */
        assertEquals(0, team.getActiveUnitIndex());
    }

    @Test
    public void testendTurn() {
        /**
         * end turn for team, add new active unit index for team
         */
        team.endTurn();
        // If we end the turn, the active unit index will be set +1. So AlphaUnit2 will now be active.
        assertEquals(1, team.getActiveUnitIndex());
    }
}
