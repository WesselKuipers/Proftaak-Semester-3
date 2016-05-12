package com.wotf.game.classes;

import com.wotf.game.GameStage;
import static com.wotf.game.classes.GameSettings.WEAPONS_ARMORY;
import com.wotf.game.classes.Items.Item;
import com.wotf.game.classes.TurnLogic.TurnState;
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
    private final List<Team> teams;
    private final Map map;

    private final GamePhysics gamePhysics;
    private final GameSettings gameSettings;
    private final TurnLogic turnLogic;

    /**
     * Constructor of Game, assign params to properties. Add new game physics
     * and add a turn logic based on amount of teams
     *
     * @param gameSettings
     * @param map
     * @param players
     */
    public Game(GameSettings gameSettings, Map map, List<Player> players) {
        this.gameSettings = gameSettings;
        this.host = players.get(0);
        this.players = players;
        this.teams = this.gameSettings.getTeams();
        this.gamePhysics = new GamePhysics();
        this.turnLogic = new TurnLogic(this.teams.size());
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
     * Set keyboard & camera focus to active unit
     */
    public void beginTurn() {
        map.calculateWind();
        
        Team activeTeam = getActiveTeam();
        turnLogic.beginTurn();
        activeTeam.beginTurn();
        setActiveUnit(activeTeam);
    }
    
    /**
     * Set the controls to the active unit
     * @param team the current active team
     */
    private void setActiveUnit(Team team) {
        if (team.getActiveUnit().getHealth() > 0) {
            GameStage gameStage = (GameStage) team.getActiveUnit().getStage();
            gameStage.setKeyboardFocus(team.getActiveUnit());
            gameStage.setCameraFocusToActor(team.getActiveUnit(), true);

            // select first weapon
            Item i = WEAPONS_ARMORY.get(0);
            team.getActiveUnit().selectWeapon(i);
        } else {
            endTurn();
        }
    }

    /**
     * Method to end a turn in the game. First call the endTurn method of the
     * active team and turn logic, After that select the new active team and set
     * the active index of the team to keyboard and camera focus. Last check
     * whether team and its units are still alive.
     */
    public void endTurn() {
        Team activeTeam = getActiveTeam();
        activeTeam.endTurn();
        turnLogic.endTurn();
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
            turnLogic.setState(TurnState.GAMEOVER);
        }
    }
    
    public void update(Float deltaTime) {
        turnLogic.update(deltaTime);
        
        // If unit hasn't fired yet after the turn time, end the current round and start a new one 
        if (turnLogic.getElapsedTime() >= gameSettings.getTurnTime()) {
            endTurn();
        }
        
        // When the player has passed 
        if (turnLogic.getState() == TurnState.WITHDRAW) {
            if(turnLogic.getElapsedTime() >= gameSettings.getWithdrawTime()) {
                beginTurn();
            }
        }
        
        // If max time has exceeded end the game
        if (turnLogic.getTotalTime() >= gameSettings.getMaxTime()) {
            endTurn();
            turnLogic.setState(TurnState.GAMEOVER);
        }
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
}
