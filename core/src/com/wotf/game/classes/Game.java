package com.wotf.game.classes;

import com.wotf.game.GameStage;
import static com.wotf.game.classes.GameSettings.WEAPONS_ARMORY;
import com.wotf.game.classes.Items.Item;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main data structure used to contain all data required to start and run a game session
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
        if(teams.isEmpty()) {
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
        turnLogic.beginTurn();
        Team activeTeam = getActiveTeam();
        GameStage gameStage = (GameStage) activeTeam.getActiveUnit().getStage();
        gameStage.setKeyboardFocus(activeTeam.getActiveUnit());
        gameStage.setCameraFocusToActor(activeTeam.getActiveUnit(), true);
        
        Item i = WEAPONS_ARMORY.get(0);
        activeTeam.getActiveUnit().selectWeapon(i);
    }

    /**
     * Method to end a turn in the game.
     * First call the endTurn method of the active team and turn logic,
     * After that select the new active team and set the active index of the team to keyboard and camera focus.
     * Last check whether team and its units are still alive.
     */
    public void endTurn() {
        Team activeTeam = getActiveTeam();
        activeTeam.endTurn();
        turnLogic.endTurn();
        List<Team> teamsToRemove = new ArrayList<>();
        List<Unit> unitsToRemove = new ArrayList<>();
        
        // TODO: Remove units and teams based on health and remaining unit count
        // When unit has lower or equal than 0 health, remove the unit from the team
        for (Team team : teams) {
            int unitsAlive = team.getUnits().size();
            for (Unit unit : team.getUnits()) {
                if (unit.getHealth() <= 0) {
                    unitsToRemove.add(unit);
                    unitsAlive--;
                }  
            }
            // Remove team when no units are alive
            if (unitsAlive <= 0) {
                teamsToRemove.add(team);
            }
        }
        
        for (int i = 0; i < unitsToRemove.size(); i++) {
            for(Team t : teams) {
                t.removeUnit(unitsToRemove.get(i));
            }
        }
        
        teams.removeAll(teamsToRemove);
        for (int i = 0; i < teamsToRemove.size(); i++) {
            turnLogic.lowerTeamCount();
        }
        
        // Game over
        if (teams.size() <= teamsToRemove.size()) {
            turnLogic.gameOverState();
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