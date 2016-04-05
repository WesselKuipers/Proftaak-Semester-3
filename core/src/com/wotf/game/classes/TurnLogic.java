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
    private float elapsedTime;
    private int turn;
    private int totalTeams;

    public TurnLogic(int totalTeams) {
        this.elapsedTime = 0;
        this.turn = 0;
        this.totalTeams = totalTeams;
    }
    
    public float getElapsedTime() {
        return Math.round(elapsedTime);
    }
    
    public void update(float deltaTime) {
        elapsedTime = elapsedTime + deltaTime;
    }
    
    public void endTurn() {
        this.elapsedTime = 0;
        this.turn++;
    }

    public int getTurn() {
        return turn;
    }

    public int getActiveTeamIndex() {
        return turn % totalTeams;
    }
    
    public void lowerTeamCount() {
        totalTeams--;
    }
}
