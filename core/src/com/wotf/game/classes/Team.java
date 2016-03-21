package com.wotf.game.classes;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Wessel on 14/03/2016.
 */
public class Team {
    private String name;
    private Color color;
    private List<Player> players;
    private List<Unit> units;
    private Map<Item, Integer> items; // The integer represents the ammo remaining

    public Team(String name, Color color) {
        this.name = name;
        this.color = color;

        // TODO: Instantiating list of items
        items = new HashMap<>();
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void removePlayer(Player p) {
        players.remove(p);
        // TODO: Logic for kicking player out
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<Unit> getUnits() {
        return Collections.unmodifiableList(units);
    }

    public Unit getUnit(int index) {
        return units.get(index);
    }

    public void addUnit(String name, int health) {
        units.add(new Unit(name, health));
    }

    public void removeUnit(Unit unit) {
        units.remove(unit);

        // TODO: Logic for killing units?
    }
}
