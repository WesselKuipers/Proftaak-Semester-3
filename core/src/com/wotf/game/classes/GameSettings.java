/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.wotf.game.classes.Items.*;
import com.wotf.game.classes.Items.Item;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rensphilipsen
 */
public class GameSettings {

    public static List<Item> WEAPONS_ARMORY;
    private List<Team> teams;

    private int maxTurns;
    private int maxTime;
    private int turnTime;
    private int withdrawTime;
    private boolean fallingDamage;
    private boolean suddenDeath;

    public GameSettings() {
        WEAPONS_ARMORY = new ArrayList<>();
        fillWeapons();

        teams = new ArrayList<>();

        maxTurns = 30;
        maxTime = 30 * 60;
        turnTime = 30;
        withdrawTime = 3;
        fallingDamage = true;
        suddenDeath = true;
    }

    /**
     * Not dynamic list atm. will be replaced with user input in later itt.
     */
    private void fillWeapons() {
        Sprite sprite = new Sprite(new Texture(Gdx.files.internal("BulletBill.png")));
        WEAPONS_ARMORY.add(new Bazooka("Bazooka", 40, 10, sprite, sprite));
        WEAPONS_ARMORY.add(new Bazooka("Grenade", 40, 10, sprite, sprite));
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
