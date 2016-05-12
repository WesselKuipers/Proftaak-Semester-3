package com.wotf.game.classes;

/**
 * Represents a player connected in the session
 */
public class Player {

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
