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

    /**
     * Enum State of the game
     */
    public enum TurnState {
        PLAYING, WITHDRAW, GAMEOVER
    };
    
    public TurnState currentState;
    private float elapsedTime;
    private float totalTime;
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
        this.totalTime = 0;
        this.turn = 0;
        this.totalTeams = totalTeams;
        this.currentState = TurnState.PLAYING;
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
    }
    
    /**
     * End the turn, set the elapsed time to zero
     * Add to turn count and set the current state to withdraw
     */
    public void endTurn() {
        this.elapsedTime = 0;
        this.turn++;
        setState(TurnState.WITHDRAW);
    }
    
    /**
     * Begin turn, set elapsed time to zero
     * set the current state to playing
     */
    public void beginTurn() {
        this.elapsedTime = 0;
        setState(TurnState.PLAYING);
    }
}