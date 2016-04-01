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

    private int elapsedTime;
    private int turn;
    private int totalTeams;

    public TurnLogic(int totalTeams) {
        this.elapsedTime = 0;
        this.turn = 1;
        this.totalTeams = totalTeams;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void endTurn() {
        // TODO: Jip weet niet precies wt er moet gebeuren. Rens denkt niet zelf na.
    }

    public int getTurn() {
        return turn;
    }

    public int getActiveTeamIndex() {
        return turn % totalTeams;
    }
}
