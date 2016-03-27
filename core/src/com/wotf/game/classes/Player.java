package com.wotf.game.classes;

/**
 * Created by Wessel on 14/03/2016.
 */
public class Player {
    private String ip;
    private String name;

    public Player(String ip, String name) {
        this.ip = ip;
        this.name = name;
    }
    
    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
