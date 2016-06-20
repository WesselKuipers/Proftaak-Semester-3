package com.wotf.game.classes;

import java.io.Serializable;

/**
 * Represents a player connected in the session
 */
public class Player implements Serializable{

    private int id;
    private final String ip;
    private String name;

    /**
     * Main constructor of a player
     * @param ip String representing the IP address of the player
     * @param name String representing the username of the player
     */
    public Player(String ip, String name) {
        this.ip = ip;
        this.name = name;
    }

    /**
     * Returns the IP address of the player
     * @return String representing an IP address
     */
    public String getIp() {
        return ip;
    }

    /**
     * Returns the name associated with this player
     * @return String representing the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the player to a new name
     * @param name Name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The ID of the player
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the player
     * @param id ID nr to set
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
