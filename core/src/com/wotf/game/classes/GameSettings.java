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
 * Contains the settings used to determine some of the rules the game has to
 * follow
 */
public class GameSettings {

    /**
     * List of weapons in the game
     */
    public static List<Item> WEAPONS_ARMORY;
    /**
     * List of teams in the game
     */
    private final List<Team> teams;

    private int maxTurns;
    private int maxTime;
    private int turnTime;
    private int withdrawTime;
    private boolean fallingDamage;
    private boolean suddenDeath;

    /**
     * Main constructor for GameSettings Sets all variables to the default rules
     */
    public GameSettings() {
        if (WEAPONS_ARMORY == null) {
            WEAPONS_ARMORY = new ArrayList<>();
            fillWeapons();
        }

        teams = new ArrayList<>();

        maxTurns = 30;
        maxTime = 30 * 60;
        turnTime = 30;
        withdrawTime = 3;
        fallingDamage = true;
        suddenDeath = true;
    }

    /**
     * Constructor without any graphics Made for the unit testing.
     */
    public GameSettings(boolean any) {
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
     * TODO!
     */
    private void fillWeapons() {
        Sprite bullet_sprite = new Sprite(new Texture(Gdx.files.internal("BulletBill.png")));
        Sprite bazooka_sprite = new Sprite(new Texture(Gdx.files.internal("Bazooka.png")));
        
        Sprite grenade_sprite = new Sprite(new Texture(Gdx.files.internal("grenade.png")));
        Sprite clusterbomb_sprite = new Sprite(new Texture(Gdx.files.internal("clusterbomb.png")));
        
        Sprite nuke_sprite = new Sprite(new Texture(Gdx.files.internal("nuclearbomb.png")));
        Sprite remote_sprite = new Sprite(new Texture(Gdx.files.internal("remote.png")));

        WEAPONS_ARMORY.add(new Bazooka("Bazooka", 15, 30, 40, bazooka_sprite, bullet_sprite));
        WEAPONS_ARMORY.add(new Nuke("Nuke", 5, 60, 80, remote_sprite, nuke_sprite));
        WEAPONS_ARMORY.add(new Grenade("Grenade", 10, 10, 20, grenade_sprite, grenade_sprite));
        WEAPONS_ARMORY.add(new Clusterbomb("Clusterbomb", 10, 20, 45, clusterbomb_sprite, clusterbomb_sprite));
    }

    /**
     * Returns the max turns of the game
     *
     * @return int of max turns
     */
    public int getMaxTurns() {
        return maxTurns;
    }

    /**
     * Sets the max turns of the game
     *
     * @param maxTurns int to set the max turns
     */
    public void setMaxTurns(int maxTurns) {
        this.maxTurns = maxTurns;
    }

    /**
     * Sets the max time of the game
     *
     * @return int of the max time of the game
     */
    public int getMaxTime() {
        return maxTime;
    }

    /**
     * Sets max time of the game
     *
     * @param maxTime int to set the max time of the game
     */
    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * Gets the time of a turn
     *
     * @return int of time of a turn
     */
    public int getTurnTime() {
        return turnTime;
    }

    /**
     * Sets the time of a turn
     *
     * @param turnTime int to set time of a turn
     */
    public void setTurnTime(int turnTime) {
        this.turnTime = turnTime;
    }

    /**
     * Withdraw time is when a unit fired a weapon, he gets time moving after
     * firing. Returns the withdraw time
     *
     * @return int of the withdraw time
     */
    public int getWithdrawTime() {
        return withdrawTime;
    }

    /**
     * Withdraw time is when a unit fired a weapon, he gets time moving after
     * firing. Sets the withdraw time
     *
     * @param withdrawTime sets the withdraw time
     */
    public void setWithdrawTime(int withdrawTime) {
        this.withdrawTime = withdrawTime;
    }

    /**
     * Falling damage is when a unit is falling down.
     *
     * @return boolean if falling down
     */
    public boolean isFallingDamage() {
        return fallingDamage;
    }

    /**
     * Falling damage is when a unit is falling down.
     *
     * @param fallingDamage set true/false if falling down
     */
    public void setFallingDamage(boolean fallingDamage) {
        this.fallingDamage = fallingDamage;
    }

    /**
     * Returns if game is a sudden death match
     *
     * @return boolean if game is a sudden death match
     */
    public boolean isSuddenDeath() {
        return suddenDeath;
    }

    /**
     * Setting sudden death match
     *
     * @param suddenDeath boolean if game is sudden death
     */
    public void setSuddenDeath(boolean suddenDeath) {
        this.suddenDeath = suddenDeath;
    }

    /**
     * Add team to the game
     *
     * @param name of the team
     * @param color of the team
     */
    public void addTeam(String name, Color color) {
        teams.add(new Team(name, color));
    }

    /**
     * Add team to the game
     *
     * @param team as object to add to game
     */
    public void addTeam(Team team) {
        // Temporary overload for the default debug game instance
        teams.add(team);
    }

    /**
     * Remove team to the game
     *
     * @param t as object to remove from game
     */
    public void removeTeam(Team t) {
        teams.remove(t);
    }

    /**
     * Get teams of the game
     *
     * @return list of teams in current game
     */
    public List<Team> getTeams() {
        return teams;
    }
}
