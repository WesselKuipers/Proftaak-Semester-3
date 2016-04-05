/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

/**
 *
 * @author rensphilipsen
 */
public class TurnLogic {
    public enum TurnState {
        PLAYING, WITHDRAW
    };
    
    public TurnState currentState;
    private float elapsedTime;
    private int turn;
    private int totalTeams;

    /**
     * set the elapsed time to zero,
     * set turn to zero,
     * set the total teams to the param
     * set the current state to playing
     * @param totalTeams
     */
    public TurnLogic(int totalTeams) {
        this.elapsedTime = 0;
        this.turn = 0;
        this.totalTeams = totalTeams;
        this.currentState = TurnState.PLAYING;
    }
    
    /**
     * 
     * @return the elapsed time
     */
    public float getElapsedTime() {
        return Math.round(elapsedTime);
    }
    
    /**
     * update the elapsed time by adding the deltatime of the game
     * @param deltaTime
     */
    public void update(float deltaTime) {
        elapsedTime = elapsedTime + deltaTime;
    }
    
    /**
     * 
     * @return the current turn state
     */
    public TurnState getTurnState() {
        return currentState;
    }
    
    /**
     * End the turn, set the elapsed time to zero
     * Add to turn count and set the current state to withdraw
     */
    public void endTurn() {
        this.elapsedTime = 0;
        this.turn++;
        this.currentState = TurnState.WITHDRAW;
    }
    
    /**
     * Begin turn, set elapsed time to zero
     * set the current state to playing
     */
    public void beginTurn() {
        this.elapsedTime = 0;
        this.currentState = TurnState.PLAYING;
    }

    /**
     * 
     * @return the current turn
     */
    public int getTurn() {
        return turn;
    }

    /**
     * 
     * @return get the active team index
     */
    public int getActiveTeamIndex() {
        return turn % totalTeams;
    }
    
    public void lowerTeamCount() {
        totalTeams--;
    }
}
