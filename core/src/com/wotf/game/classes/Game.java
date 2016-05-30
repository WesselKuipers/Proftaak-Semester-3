package com.wotf.game.classes;

import com.badlogic.gdx.math.Vector2;
import com.wotf.game.GameStage;
import com.wotf.game.Networking.Command;
import com.wotf.game.Networking.NetworkMessage;
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
    private final Player playingPlayer;
    private final List<Team> teams;
    private final Map map;

    private final GameSettings gameSettings;
    private final TurnLogic turnLogic;
    
    private boolean turnState;
    
    /**
     * Constructor of Game, assign params to properties. Add new game physics
     * and add a turn logic based on amount of teams
     *
     * @param gameSettings
     * @param map
     * @param players
     * @param playingPlayer
     */
    public Game(GameSettings gameSettings, Map map, List<Player> players, Player playingPlayer) {
        this.gameSettings = gameSettings;
        this.players = players;
        this.host = this.players.get(0);
        this.playingPlayer = playingPlayer;
        this.teams = this.gameSettings.getTeams();
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
     * @return boolean if there is a current player in turn
     */
    public Boolean getTurnState(){
        return turnState;
    }

    /**
     * Function to send the current beginTurn
     */
    public void beginTurn() {
        if (playingPlayer.getId() == host.getId()) {
            GameStage gameStage = (GameStage) teams.get(0).getUnit(0).getStage();
            
            map.calculateWind();
          
            // add terrain solid booleans to parameters
       /*        boolean[][] terrain = map.getTerrain();
           for (int x = 0; x < terrain.length; x++) {
                NetworkMessage terrainMsg = new NetworkMessage( Command.TERRAIN );
                terrainMsg.addParameter("x", Integer.toString(x));
                for (int y = 0; y < terrain[0].length; y++) {
                    terrainMsg.addParameter("y"+y, Integer.toString(y));
                    terrainMsg.addParameter("val"+y, Boolean.toString(terrain[x][y]));
                }
                gameStage.getNetworkingUtil().sendToHost( terrainMsg );
            }*/
  
            // Sync units position and health
            int unitCount = 0;
            NetworkMessage syncUnitsMsg = new NetworkMessage ( Command.SYNCUNITS );

            for (Team team : teams) {
                for (Unit unit : team.getUnits()) {
                    syncUnitsMsg.addParameter("u" + unitCount + "x", Float.toString(unit.getPosition().x));
                    syncUnitsMsg.addParameter("u" + unitCount + "y", Float.toString(unit.getPosition().y));
                    syncUnitsMsg.addParameter("u" + unitCount + "hp", Integer.toString(unit.getHealth()));
                    unitCount++;
                }
            }
            
            gameStage.getNetworkingUtil().sendToHost( syncUnitsMsg );
        
            NetworkMessage beginTurnMsg = new NetworkMessage( Command.BEGINTURN );

            // add wind to parameters
            Vector2 windForce = map.getWind();
            beginTurnMsg.addParameter("windX", Float.toString(windForce.x));
            beginTurnMsg.addParameter("windY", Float.toString(windForce.y));
            
            System.out.println("Wind sent: "+windForce.x + ", "+windForce.y);

            // send message to host and after that, all clients        
            gameStage.getNetworkingUtil().sendToHost( beginTurnMsg );
            
            // run action for host too
            beginTurnReceive(windForce);
        }
    }
    
    /**
     * Function for network to receive a begin turn by the clients
     * This will set the wind, set the next active unit
     * @param windForce 
     */
    public void beginTurnReceive(Vector2 windForce) {
        turnState = true;
        Team activeTeam = getActiveTeam();
        map.setWind(windForce);
        turnLogic.beginTurn();
        activeTeam.beginTurn();
        setActiveUnit(activeTeam);
    }
    
    /**
     * Set the controls to the active unit
     * @param team the current active team
     */
    public void setActiveUnit(Team team) {
        if (team.getActiveUnit().getHealth() > 0) {
            GameStage gameStage = (GameStage) team.getActiveUnit().getStage();
            gameStage.setKeyboardFocus(team.getActiveUnit());
            gameStage.setCameraFocusToActor(team.getActiveUnit(), true);
             team.getActiveUnit().selectWeaponIndex(0);
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
        turnState = false;
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
