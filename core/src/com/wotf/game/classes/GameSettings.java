package com.wotf.game.classes;

import com.badlogic.gdx.graphics.Color;
import com.wotf.game.classes.Items.*;
import com.wotf.game.classes.Items.Item;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the settings used to determine some of the rules the game has to
 * follow
 */
public class GameSettings implements Serializable {

    /**
     * List of weapons in the game
     */
    public static final List<Item> WEAPONS_ARMORY = new ArrayList<>();;
    
    /**
     * List of teams in the game
     */
    private final List<Team> teams;

    private int mapindex;
    private String mapname;
    private int maxTurns;
    private int maxTime;
    private int turnTime;
    private int withdrawTime;
    private int maxPlayersSession;
    private int maxunitCount;
    private boolean fallingDamage;
    private boolean suddenDeath;
    private boolean physics;
    private boolean isLocal;

    /**
     * Main constructor for GameSettings Sets all variables to the default rules
     */
    public GameSettings() {
        if (WEAPONS_ARMORY.isEmpty()) {
            fillWeapons();
        }

        teams = new ArrayList<>();

        maxTurns = 30;
        maxTime = 60 * 60;
        turnTime = 40;
        withdrawTime = 3;
        maxPlayersSession = 2;
        fallingDamage = true;
        suddenDeath = true;
        physics = true;
    }

    /**
     * Not dynamic list atm. will be replaced with user input in later it.
     */
    private void fillWeapons() {
        WEAPONS_ARMORY.add(new Bazooka());
        WEAPONS_ARMORY.add(new Nuke());
        WEAPONS_ARMORY.add(new Grenade());
        WEAPONS_ARMORY.add(new Clusterbomb());
        WEAPONS_ARMORY.add(new AirStrike());
    }
 
    /**
     * Sets the index of the map with the settings
     *
     * @param mapIndex index nr of the map to load
     */
    public void setMapIndex(int mapIndex) {
        this.mapindex = mapIndex;
    }

    /**
     * Gets the index of the map
     *
     * @return the index of the name
     */
    public int getMapIndex() {
        return mapindex;
    }

    /**
     * Sets the name of the map with the settings
     *
     * @param mapname
     */
    public void setMapName(String mapname) {
        this.mapname = mapname;
    }

    /**
     * Gets the name of the map
     *
     * @return the String of the name
     */
    public String getMapName() {
        return mapname;
    }

    /**
     * Sets the physics of the game
     *
     * @param physics
     */
    public void setPhysics(boolean physics) {
        this.physics = physics;
    }

    /**
     * Returns the physics of the game
     *
     * @return boolean if the physics are turned on or not.
     */
    public boolean getPhysics() {
        return physics;
    }

    /**
     * Sets the maximum amount of players allowed in a session
     * @param maxPlayers Max amount of players allowed
     * @throws RemoteException thrown when there's a connection error
     */
    public void setMaxPlayersSession(int maxPlayers) throws RemoteException {
        this.maxPlayersSession = maxPlayers;
    }

    /**
     * @return The maximum amount of players allowed in a session
     */
    public int getMaxPlayersSession() {
        return maxPlayersSession;
    }
    
    /**
     * Sets the maximum amount of units allowed in a team
     * @param maxUnitCount 
     */
    public void setMaxUnitCount(int maxUnitCount){
        this.maxunitCount = maxUnitCount;
    }
    
    public int getMaxUnitCount(){
        return this.maxunitCount;
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
        this.maxTime = maxTime * 60;
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
    
    /**
     * @return True if session is hosted locally
     */
    public boolean getIsLocal() {
        return isLocal;
    }

    /**
     * @param isLocal Sets isLocal variable
     */
    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }
}
