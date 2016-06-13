/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import HeadlessRunner.GdxTestRunner;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.wotf.game.classes.Items.Bazooka;
import com.wotf.game.classes.Items.Item;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 *
 * @author Remco
 */
@RunWith(GdxTestRunner.class)
public class TeamTest {

    private Team team;
    private GameSettings gamesettings;
    private Player player;
    private Item item;

    @Before
    public void initTeam() {
        /**
         * Constructor of Team, Initialize lists and set active unit index to
         * zero.
         *
         * @param name
         * @param color
         */
        item = new Bazooka("Bazooka");
        gamesettings = new GameSettings();
        player = new Player("127.0.0.1", "Wessel");
        team = new Team("Alpha", Color.BLACK);
        //team.addPlayer(player);
        team.addUnit("AlphaUnit", 50);
        team.addUnit("AlphaUnit2", 80);
        gamesettings.addTeam(team);
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", team);
    }

    @Test
    public void testsetandgetPlayer() {
        /**
         * Adds a player to team
         *
         * @param p player
         */
        // First add a new player.
        Player rens = new Player("127.0.0.2", "Rens");
        team.setPlayer(rens);
        // Test if the size is 2 now. This means it's added.
        assertEquals(rens, team.getPlayer());
    }

    @Test
    public void testsetandgetColorname() {
        // Set a colorname 
        team.setColorname("color");
        // Check if nothing goes wrong..
        assertEquals("color", team.getColorname());
    }

    @Test
    public void testgetandsetName() {
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
    public void testmakeUnitList() {
        // Make new list because there already are units for 'team'.
        Team team2 = new Team("Gamza", Color.BLACK);
        // Test if the list indeed is null before.
        // It will only be if a team sends a list which does have a unit list as transient
        assertNull(team2.getUnits());
        // Then make the list..
        team2.makeUnitList();
        // Check if it still is null or not.
        assertEquals(0, team2.getUnits().size());
    }

    @Test
    public void testgetActiveUnit() {
        /**
         *
         * @return active unit by active unit index
         */
        // The name of the unit which is added to the team.
        String unitname = "AlphaUnit";
        team.setActiveUnit(new Unit("AlphaUnit", 100, team, new Vector2(20, 20)));
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

    /**
     * Cannot be tested it uses a GameStage.
     */
//    @Test
//    public void testremoveUnit() {
//        /**
//         * When unit is killed (health is zero or lower), remove the actor and
//         * unit from team
//         *
//         * @param unit to be removed
//         */
//        // Get a unit.
//        Unit unit = team.getUnit(0);
//        // Remove the taken unit.
//        team.removeUnit(unit);
//        // Test if the list size is 0 now.
//        //assertEquals(1, team.getUnits().size());
//    }

    /**
     * Can't be tested thoroughly because it is created seperately for a client.
     * 
     */
    @Test
    public void testInitializeForClient(){
        team.intializeForClient();
    }
    
    @Test
    public void testContainsKey() {
        // Tests if the list contains this item.
        assertNotNull(team.containsKey(item));
    }

    @Test
    public void testContainsItemAmount() {
        // I just added this item 2 times.
        team.increaseItemAmount(item, 2);
        assertEquals(2, team.containsItemAmount(item));
        // Test if I can also remove it 2 times.
        // This function is bugged. Doesn't work properly.
        team.decreaseItemAmount(item, 2);
        assertEquals(0, team.containsItemAmount(item));
    }

    @Test
    public void testremoveAllUnits() {
        // Removes all the units from a team. It starts with 2.
        team.removeAllUnits();
        // The size should be 0 now.
        assertEquals(0, team.getUnits().size());
    }

    @Test
    public void testNextActiveUnit() {
        /**
         * Set the next active unit in the team
         */
        team.setNextActiveUnit();
        // The first unit is set, because it starts at null. 
        assertEquals("AlphaUnit", team.getActiveUnit().getName());
        // This means if I do it 2 times.
        team.setNextActiveUnit();
        assertEquals("AlphaUnit2", team.getActiveUnit().getName());
        // Now remove all units and see if it will be the old unit.
        team.removeAllUnits();
        team.setNextActiveUnit();
        assertNull(team.getActiveUnit());
    }

    @Test
    public void testendTurn() {
        /**
         * end turn for team When unit has lower or equal than 0 health, remove
         * the unit from the team
         */
        // Add a unit with 0 hp. And check the size.
        team.addUnit("AlphaUnit3", 0);
        assertEquals(3, team.getUnits().size());
        // End the turn and see if the unit with 0 hp is removed.
        team.endTurn();
        assertEquals(2, team.getUnits().size());
    }

    @Test
    public void testbeginTurn() {
        Team team2 = new Team("Kolor", Color.GRAY);
        team2.addUnit("UInit1", 22);
        team2.addUnit("UInit2", 44);
        // Begin the turn
        team2.beginTurn();
        // Now the unit with index 0 is set. AlphaUnit is the first one added. And first one served.
        assertEquals("UInit1", team2.getActiveUnit().getName());
        // Now switch turn.
        team2.beginTurn();
        // Now the unit index 1 is set. If it goes right. This is UInit2.
        assertEquals("UInit2", team2.getActiveUnit().getName());
    }
}
