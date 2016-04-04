package com.wotf.game.classes;

import com.wotf.game.classes.Items.Item;
import com.badlogic.gdx.graphics.Color;
import com.wotf.game.classes.Items.*;
import java.util.ArrayList;
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
    private int activeUnitIndex;

    public Team(String name, Color color) {
        this.name = name;
        this.color = color;

        //Instantiating list of items
        items = new HashMap<>();
        players = new ArrayList<>();
        units = new ArrayList<>();
        
        // Select first unit of team as active unit
        this.activeUnitIndex = 0;
        
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
        units.add(new Unit(name, health, this));
    }

    public void removeUnit(Unit unit) {

        units.remove(unit);

        // TODO: Logic for killing units?
    }
    
    public int getActiveUnitIndex() {
        return activeUnitIndex;
    }

    public void endTurn() {
        // Change the active unit index if its not at the end of the list
        if (activeUnitIndex < (units.size() - 1)) {
            activeUnitIndex++;
        } else {
            activeUnitIndex = 0;
        }
    }

    public void setActiveUnit(Unit u) {
        //TODO
    }

    public Item selectItem(int item) {
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            if (entry.getKey().getName().toLowerCase().equals((EnumItems.values()[item]).toString().toLowerCase())) {
                return (Item) entry.getKey();
            }
        }
        return null;
    }

    public void increaseItemAmount(Item item, int amount) {
        items.put(item, amount);
    }

    public void decreaseItemAmount(Item item, int amount) {
        if (items.containsKey(item)) {
            if (items.get(item) > 0) {
                increaseItemAmount(item, (items.get(item) - 1));
            }
            if (items.get(item) == 0 || items.get(item) < 0) {
                //TODO: handle what happens when unlimited ammo (-1) or out of ammo
               
            }
        }
    }

    public int containsItemAmount(Item item) {
        if (items.containsKey(item)) {
            return items.get(item);
        }
        return 0;
    }

    @Override
    public String toString() {
        return getName() + " - " + getColor();
    }
}
