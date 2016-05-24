/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.wotf.game.classes.TurnLogic.TurnState;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Remco
 */
public class TurnLogicTest {

    private TurnLogic turnlogic;

    @Before
    public void initTurnLogic() {
        turnlogic = new TurnLogic(3);
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", turnlogic);
    }

    @Test
    public void testgetElapsedTime() {
        // Test if the elapsed time is 0. At this moment I'm not testing with the built in timer.
        assertEquals(0, (int) turnlogic.getElapsedTime());
    }

    @Test
    public void testgetTotalTime() {
        // Test if the total time is 0. At this moment I'm not testing with the built in timer.
        assertEquals(0, (int) turnlogic.getTotalTime());
    }

    @Test
    public void testgetTurn() {
        // The default turn is 0 with the constructor.
        assertEquals(0, turnlogic.getTurn());
    }

    @Test
    public void testgetTotalTeams() {
        // The total teams is set on 3 in the constructor
        assertEquals(3, turnlogic.getTotalTeams());
    }

    @Test
    public void testsetTotalTeams() {
        // Set the total teams to 5
        turnlogic.setTotalTeams(5);
        // Check if it really is set to 5.
        assertEquals(5, turnlogic.getTotalTeams());
    }

    @Test
    public void testgetState() {
        // Get the current state, this is set on Playing in the constructor.
        assertEquals(TurnState.PLAYING, turnlogic.getState());
    }

    @Test
    public void testsetState() {
        // Sets the state to something different than Playing.
        turnlogic.setState(TurnState.GAMEOVER);
        // Get the new state to see if it matches with the previously given in state.
        assertEquals(TurnState.GAMEOVER, turnlogic.getState());
    }

    @Test
    public void testUpdate() {
        // Test if the update is working properly. 
        // I pass 3 as parameter deltatime.
        turnlogic.update(3);
        // If I now get the elapsedTime it should be 3. ( 0 + 3 = 3 )
        assertEquals(3, (int) turnlogic.getElapsedTime());
    }

    @Test
    public void testendTurn() {
        // Test if the field 'turn' has became 1 instead of 0.
        turnlogic.endTurn();
        assertEquals(1, turnlogic.getTurn());
    }

    @Test
    public void testendTurnState() {
        // Test to see if the State is correctly setting to Withdraw when ending a turn.
        turnlogic.endTurn();
        assertEquals(TurnState.WITHDRAW, turnlogic.getState());
    }

    @Test
    public void testbeginTurnState() {
        // Test to see if the State is correctly setting to Withdraw when ending a turn.
        turnlogic.beginTurn();
        assertEquals(TurnState.PLAYING, turnlogic.getState());
    }

    @Test
    public void testactiveTeamIndex() {
        // This will return the same int as the turn.
        // I've ended the turn twice to give a different example as given above. Now it will return 2.
        turnlogic.endTurn();
        turnlogic.endTurn();
        assertEquals(2, turnlogic.getActiveTeamIndex());
    }

}
