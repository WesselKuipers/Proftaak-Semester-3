package com.wotf.game.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main data structure used to contain all data required to start and run a game
 * session
 */
public class Game {

    private final Player host;
    private final List<Player> players;
    private final Player playingPlayer;
    private final List<Team> teams;
    private final Map map;

    private final GameSettings gameSettings;
    private final TurnLogic turnLogic;
    
    /**
     * Constructor of Game, assign params to properties. Add new game physics
     * and add a turn logic based on amount of teams
     *
     * @param gameSettings
     * @param map
     * @param players
     * @param playingPlayer
     * @param host
     */
    public Game(GameSettings gameSettings, Map map, List<Player> players, Player playingPlayer, Player host) {
        this.gameSettings = gameSettings;
        this.players = players;
        this.host = host;
        this.playingPlayer = playingPlayer;
        this.teams = this.gameSettings.getTeams();
        this.turnLogic = new TurnLogic(this, this.teams.size());
        this.map = map;
    }

    /**
     *
     * @return list of all players
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     *
     * @param ip ip of the player
     * @return player
     */
    public Player getPlayer(String ip) {
        return players
                .stream()
                .filter(x -> x.getIp().equals(ip))
                .findFirst()
                .get();
    }

    /**
     *
     * @param index index of player
     * @return player by index
     */
    public Player getPlayer(int index) {
        return players.get(index);
    }

    /**
     *
     * @return the player that is currently playing the game
     */
    public Player getPlayingPlayer() {
        return playingPlayer;
    }

    /**
     *
     * @return the player that is host
     */
    public Player getHost() {
        return host;
    }

    /**
     *
     * @return list of teams
     */
    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    /**
     *
     * @param index index of team
     * @return team by index
     */
    public Team getTeam(int index) {
        return teams.get(index);
    }

    /**
     *
     * @return the active team used by the active team index
     */
    public Team getActiveTeam() {
        if (teams.isEmpty()) {
            return null;
        }
        return teams.get(turnLogic.getActiveTeamIndex());
    }

    /**
     *
     * @return the map
     */
    public Map getMap() {
        return map;
    }

    /**
     *
     * @return the game settings
     */
    public GameSettings getGameSettings() {
        return gameSettings;
    }

    /**
     *
     * @return the turn logic
     */
    public TurnLogic getTurnLogic() {
        return turnLogic;
    }
    
    /**
     * Removes teams from the game when they have no units left
     * Sets the game over state if a winning condition is found
     */
    public void removeTeamsToBeRemoved() {
        List<Team> teamsToRemove = new ArrayList<>();
        
        for(Team team : teams) {
            // Remove team when no units are alive
            if (team.getUnits().size() <= 0) {
                teamsToRemove.add(team);
            }
        }
        turnLogic.setTotalTeams(turnLogic.getTotalTeams() - teamsToRemove.size());
        teams.removeAll(teamsToRemove);
        
        // Game over
        if (teams.size() <= teamsToRemove.size()) {
            turnLogic.setState(TurnLogic.TurnState.GAMEOVER);
        }
    }
}
