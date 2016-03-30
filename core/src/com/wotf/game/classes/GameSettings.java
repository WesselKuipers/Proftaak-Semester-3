/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rensphilipsen
 */
public class GameSettings {

    private List<Team> teams;

    private int maxTurns;
    private int maxTime;
    private int turnTime;
    private int withdrawTime;
    private boolean fallingDamage;
    private boolean suddenDeath;

    public GameSettings() {
        teams = new ArrayList<>();

        maxTurns = 30;
        maxTime = 30 * 60;
        turnTime = 30;
        withdrawTime = 3;
        fallingDamage = true;
        suddenDeath = true;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public void setMaxTurns(int maxTurns) {
        this.maxTurns = maxTurns;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public int getTurnTime() {
        return turnTime;
    }

    public void setTurnTime(int turnTime) {
        this.turnTime = turnTime;
    }

    public int getWithdrawTime() {
        return withdrawTime;
    }

    public void setWithdrawTime(int withdrawTime) {
        this.withdrawTime = withdrawTime;
    }

    public boolean isFallingDamage() {
        return fallingDamage;
    }

    public void setFallingDamage(boolean fallingDamage) {
        this.fallingDamage = fallingDamage;
    }

    public boolean isSuddenDeath() {
        return suddenDeath;
    }

    public void setSuddenDeath(boolean suddenDeath) {
        this.suddenDeath = suddenDeath;
    }

    public void addTeam(String name, Color color) {
        teams.add(new Team(name, color));
    }

    public void addTeam(Team team) {
        // Temporary overload for the default debug game instance
        teams.add(team);
    }

    public void removeTeam(Team t) {
        teams.remove(t);
    }

    public List<Team> getTeams() {
        return teams;
    }
}
