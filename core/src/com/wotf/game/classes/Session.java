package com.wotf.game.classes;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Wessel on 14/03/2016.
 */
public class Session {
    private GameSettings gameSettings;
    private Player host;
    private List<Player> players;

    public Session(Player host) {
        this.gameSettings = new GameSettings();
        this.host = host;
        this.players = new ArrayList<>();
    }

    public Player getHost() {
        return host;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void removePlayer(Player p) {
        players.remove(p);
        // TODO: logic for kicking players
    }

    public void startGame() {
        // TODO: handle code for creating game object
    }
}
