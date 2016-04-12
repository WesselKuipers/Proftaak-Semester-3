package com.wotf.game.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Session contains the data structure containing the list of teams, players and
 * gameSettings
 */
public class Session {

    private final GameSettings gameSettings;
    private final Player host;
    private final List<Player> players;

    /**
     * Initializes a session using the information of the hosting player
     *
     * @param host Player indicating which player is the host
     */
    public Session(Player host) {
        this.gameSettings = new GameSettings();
        this.host = host;
        this.players = new ArrayList<>();
    }

    /**
     * Constructor without any graphics Made for the unit testing.
     */
    public Session(Player host, boolean any){
        this.gameSettings = new GameSettings(true);
        this.host = host;
        this.players = new ArrayList<>();
    }
    
    /**
     * @return The host of this session
     */
    public Player getHost() {
        return host;
    }

    /**
     * @return List of players that joined this session
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Adds a player to the list of players
     *
     * @param p Player to add
     */
    public void addPlayer(Player p) {
        players.add(p);
    }

    /**
     * Removes a player from the list of players Will kick the player from the
     * lobby afterwards
     *
     * @param p Player to remove
     */
    public void removePlayer(Player p) {
        players.remove(p);
        // TODO: logic for kicking players
    }

    /**
     * Initializes the game screen
     */
    public void startGame() {
        // TODO: handle code for creating game object
    }
}
