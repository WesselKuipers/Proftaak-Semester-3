/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.math.Vector2;
import com.wotf.game.GameStage;
import com.wotf.game.Networking.Command;
import com.wotf.game.Networking.NetworkMessage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rensphilipsen
 */
public class TurnLogic {
    
    /**
     * Enum State of the game
     */
    public enum TurnState {
        PLAYING, WITHDRAW, GAMEOVER, SHOOTING
    };
    
    public TurnState currentState;
    private float elapsedTime;
    private float totalTime;
    private int turn;
    private int totalTeams;
    private final Game game;

    /**
     * set the elapsed time to zero,
     * set turn to zero,
     * set the total teams to the param
     * set the current state to playing
     * @param game
     * @param totalTeams
     */
    public TurnLogic(Game game, int totalTeams) {
        this.elapsedTime = 0;
        this.totalTime = 0;
        this.turn = 0;
        this.totalTeams = totalTeams;
        this.currentState = TurnState.PLAYING;
        this.game = game;
    }
    
    /**
     * @return the elapsed time
     */
    public float getElapsedTime() {
        return Math.round(elapsedTime);
    }
    
    /**
     * 
     * @return the amount of time that passed since the beginning of the game in seconds
     */
    public float getTotalTime() {
        return Math.round(totalTime);
    }
    
    /**
     * 
     * @return the current turn
     */
    public int getTurn() {
        return turn;
    }
    
    /**
     * set the total teams
     * @param totalTeams 
     */
    public void setTotalTeams(int totalTeams) {
        this.totalTeams = totalTeams;
    }
    
    /**
     * 
     * @return the total teams
     */
    public int getTotalTeams() {
        return totalTeams;
    }

    /**
     * 
     * @return the current turn state
     */
    public TurnState getState() {
        return currentState;
    }
    
    /**
     * Set the current state
     * @param state
     */
    public void setState(TurnState state) {
        this.currentState = state;
    }
    
    /**
     * 
     * @return get the active team index
     */
    public int getActiveTeamIndex() {
        return turn % totalTeams;
    }
    
    /**
     * update the elapsed time by adding the deltatime of the game
     * @param deltaTime
     */
    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        totalTime += deltaTime;
        
        // If unit hasn't fired yet after the turn time, end the current round and start a new one 
        if (elapsedTime >= game.getGameSettings().getTurnTime()) {
            endTurn();
        }
        
        // When the player has passed 
        if (getState() == TurnState.WITHDRAW) {
            if(elapsedTime >= game.getGameSettings().getWithdrawTime()) {
                beginTurn();
            }
        }
        
        // If max time has exceeded end the game
        if (totalTime >= game.getGameSettings().getMaxTime()) {
            endTurn();
            setState(TurnState.GAMEOVER);
        }
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
     * Begin turn, set elapsed time to zero
     * set the current state to playing
     */
    public void beginTurn() {
         if (game.getPlayingPlayer().getId() == game.getHost().getId()) {
            GameStage gameStage = (GameStage) game.getTeams().get(0).getUnit(0).getStage();
            
            game.getMap().calculateWind();
        
            NetworkMessage beginTurnMsg = new NetworkMessage(Command.BEGINTURN);

            // add wind to parameters
            Vector2 windForce = game.getMap().getWind();
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
        this.elapsedTime = 0;
        setState(TurnState.PLAYING);
        
        Team activeTeam = game.getActiveTeam();
        game.getMap().setWind(windForce);
        
        if (activeTeam.getActiveUnit() == null) {
            activeTeam.setActiveUnit(activeTeam.getUnits().get(0));
        } else {
            game.getActiveTeam().setNextActiveUnit();
        }
        setActiveUnit(activeTeam);
    }
    
    /**
     * End the turn, set the elapsed time to zero
     * Add to turn count and set the current state to withdraw
     */
    public void endTurn() {
        // We only want to run this function by the playing player
        if (game.getPlayingPlayer().getId() == game.getActiveTeam().getPlayer().getId()) {
            GameStage gameStage = (GameStage) game.getTeams().get(0).getUnit(0).getStage();

            // Sync units position and health and send a message
            int unitCount = 0;
            NetworkMessage syncUnitsMsg = new NetworkMessage(Command.SYNCUNITS);

            for (Team team : game.getTeams()) {
                for (Unit unit : team.getUnits()) {
                    syncUnitsMsg.addParameter("u" + unitCount + "x", Float.toString(unit.getPosition().x));
                    syncUnitsMsg.addParameter("u" + unitCount + "y", Float.toString(unit.getPosition().y));
                    syncUnitsMsg.addParameter("u" + unitCount + "hp", Integer.toString(unit.getHealth()));
                    unitCount++;
                }
            }

            gameStage.getNetworkingUtil().sendToHost(syncUnitsMsg);

            // Send end turn message
            NetworkMessage endTurnMsg = new NetworkMessage(Command.ENDTURN);
            gameStage.getNetworkingUtil().sendToHost(endTurnMsg);
            
            endTurnReceive();
        }
    }
    
    /**
     * Method to end a turn in the game. First call the endTurn method of the
     * active team and turn logic, After that select the new active team and set
     * the active index of the team to keyboard and camera focus. Last check
     * whether team and its units are still alive.
     */
    public void endTurnReceive() {
        this.elapsedTime = 0;
        this.turn++;
        setState(TurnState.WITHDRAW);
        
        game.getActiveTeam().removeUnitsToBeRemoved();
        game.removeTeamsToBeRemoved();
    }
}