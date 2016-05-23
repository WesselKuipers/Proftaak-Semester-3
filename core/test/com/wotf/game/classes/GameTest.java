/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Remco
 */
public class GameTest {

    private Game game;
    private Player playerrens;
    private Player playerdino;
    private Team alpha;
    private Team beta;
    private GameSettings gamesetting;

    @Before
    public void testGame() {
        // Make a new GameSettings object.
        gamesetting = new GameSettings(true);
        // Make 2 teams.
        alpha = new Team("Alpha", Color.RED, true);
        // Add a unit to both the teams.
        // alpha.addUnit("AlphaUnit", 100);
        beta = new Team("Beta", Color.GREEN, true);
        // beta.addUnit("BetaUnit", 150);
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
        game = new Game(gamesetting, null, players, playerrens);
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", game);
    }

    @Test
    public void testgetPlayers() {
        // Two players 'Rensje', 'Dinotje' has been added. The size should be two.
        assertEquals(2, game.getPlayers().size());
    }

    @Test
    public void testgetPlayerIP() {
        // Get the player which has the given IP.
        Player selplayer = game.getPlayer("127.0.0.1");
        assertEquals(playerrens, selplayer);

        // Test if it fails for another player.
        assertNotEquals(playerdino, selplayer);
    }

    @Test
    public void testgetPlayerIndex() {
        // Get the player which has the given index.
        // playerrens was the first added. This means he has the index 0.
        // playerdino was the second added. This means he has the index 1.
        // Now I'm looking for the player with index 1. This is playerdino.
        Player selplayer = game.getPlayer(1);
        assertEquals(playerdino, selplayer);

        // Test if it fails for another index.
        assertNotEquals(playerrens, selplayer);
    }

    @Test
    public void testGetPlayingPlayer() {
        // Gets the current playingplayer.
        // I added rens as playing player, it isn't switched yet.
        assertEquals(playerrens, game.getPlayingPlayer());
    }

    @Test
    public void testgetHost() {
        // The host is the player with index 0. Like in the previous testcase, playerdino is the one with index 0.
        assertEquals(playerrens, game.getHost());
    }

    @Test
    public void testgetTeams() {
        // The amount of teams in the current game. Alpha and Beta have been added so it should be 2.
        assertEquals(2, game.getTeams().size());
    }

    @Test
    public void testgetTeam() {
        // Get the team with the given index. The first team added is alpha so this will have the index 0.
        assertEquals(alpha, game.getTeam(0));
    }

    @Test
    public void testgetActiveTeam() {
        // Get the current active team. It will probably be the first one added, which is alpha.
        assertEquals(alpha, game.getActiveTeam());
    }

    @Test
    public void testMap() {
        // Get the active map. In this example it is null.
        assertNull(game.getMap());
    }
    
    @Test
    public void testgetState() {
        // The value is not set yet so it will return false probably.
        assertFalse(game.getTurnState());
    }

    @Test
    public void testEndTurn() {
        // Run the EndTurn method. After that run the getActiveTeam which should be different than the default Team 0.
        game.endTurn();
        // Test if the ActiveTeam is 1 now.
        // Can't be tested because there is no stage with actors in the testclass.
        assertEquals(beta, game.getActiveTeam());
    }

    @Test
    public void testgetGameSettings() {
        // Get the current gamesettings. Test if it is the same as the GameSettings object created above.
        assertEquals(gamesetting, game.getGameSettings());
    }

    @Test
    public void testTurnLogic() {
        // Get the current TurnLogic and test if it equals the team size.
        TurnLogic turnlogic = new TurnLogic(game.getTeams().size());
        // Can't be tested because there is nothing to compare. 
        // There is no getteamsize or whatever.
        assertEquals(turnlogic.getTurn(), game.getTurnLogic().getTurn());
    }
}
