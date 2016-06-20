/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import HeadlessRunner.GdxTestRunner;
import com.badlogic.gdx.graphics.Color;
import com.wotf.game.classes.TurnLogic.TurnState;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 *
 * @author Remco
 */
@RunWith(GdxTestRunner.class)
public class TurnLogicTest {

    private Game game;
    private Player playerrens;
    private Player playerdino;
    private Team alpha;
    private Team beta;
    private GameSettings gamesetting;
    private TurnLogic turnlogic;

    @Before
    public void initTurnLogic() {
        // Make a new GameSettings object.
        gamesetting = new GameSettings();
        // Make 2 teams.
        alpha = new Team("Alpha", Color.RED);
        alpha.addUnit("Alphasunit", 0);
        // Add a unit to both the teams.
        beta = new Team("Beta", Color.GREEN);
        beta.addUnit("Betasunit", 0);
        // Add a team to the GameSettings.
        gamesetting.addTeam(alpha);
        gamesetting.addTeam(beta);
        // Make a list of players.
        List<Player> players = new ArrayList<>();
        // Add a player to the list.
        playerrens = new Player("127.0.0.1", "Rensje");
        playerdino = new Player("2.2.2.2", "Dinotje");
        players.add(playerrens);
        players.add(playerdino);
        // Finally initialize the Game class.
        // The map is null because the map can't be initialized from the test classes.
        // It looks like a Pixmap can't be made while in the tests.
        game = new Game(gamesetting, null, players, playerrens, playerrens);
        turnlogic = game.getTurnLogic();
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
        // The total teams is set on 2 in the constructor
        assertEquals(2, turnlogic.getTotalTeams());
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

    /**
     * At the moment this fails. 
     * At the moment it tries to remove from the gamestage as well. 
     * This can't be tested without a gamestage.
     * 
     */
    @Test
    public void testEndTurnReceive() {
        // Test if the added unit with 0 hp, is removed from the team.
        // After this, the team should be removed as well, because there are no units left.
        turnlogic.endTurnReceive(2);
        // The Alpha and Beta both have 2 units with 0 hp. They should be cleaned.
        assertEquals(0, turnlogic.getTotalTeams());
    }

}
