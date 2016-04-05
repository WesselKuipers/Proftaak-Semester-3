package com.wotf.game.classes;

import com.wotf.game.classes.Items.Item;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.wotf.game.GameStage;
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

    /**
     * Constructor of Team,
     * Initialize lists and set active unit index to zero.
     * @param name
     * @param color 
     */
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

    /**
     * 
     * @return all the players of the team
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Adds a player to team
     * @param p player
     */
    public void addPlayer(Player p) {
        players.add(p);
    }

    /**
     * TODO: Logic for kicking player out
     * Removes a player from the team
     * @param p player
     */
    public void removePlayer(Player p) {
        players.remove(p);
        // TODO: Logic for kicking player out
    }

    /**
     * 
     * @return the team name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the team
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return color of team
     */
    public Color getColor() {
        return color;
    }

    /**
     * set the color of team
     * @param color color of team
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * 
     * @return all the units of team
     */
    public List<Unit> getUnits() {
        return Collections.unmodifiableList(units);
    }

    /**
     * 
     * @return active unit by active unit index
     */
    public Unit getActiveUnit() {
        return units.get(activeUnitIndex);
    }
    
    /**
     * 
     * @param index index of player
     * @return unit by index
     */
    public Unit getUnit(int index) {
        return units.get(index);
    }

    /**
     * Add a unit to the team
     * @param name name of the unit
     * @param health health of the unit
     */
    public void addUnit(String name, int health) {
        units.add(new Unit(name, health, this));
    }

    /**
     * When unit is killed (health is zero or lower), remove the actor and unit from team
     * @param unit to be removed
     */
    public void removeUnit(Unit unit) {
        if (unit == null || units.contains(unit)) { return; }
        
        GameStage gameStage = (GameStage)unit.getStage();
        int i = 0;
        for (Actor actor : gameStage.getActors()) {
            if (actor == unit) {
                units.remove(unit);
                actor.remove();
            }
            i++;
        }
    }
    
    /**
     * 
     * @return active unit index
     */
    public int getActiveUnitIndex() {
        return activeUnitIndex;
    }

    /**
     * end turn for team, add new active unit index for team
     */
    public void endTurn() {
        // Change the active unit index if its not at the end of the list
        if (activeUnitIndex < (units.size() - 1)) {
            activeUnitIndex++;
        } else {
            activeUnitIndex = 0;
        }
    }

    /**
     * TODO: Set active unit
     * @param u unit
     */
    public void setActiveUnit(Unit u) {
        //TODO
    }

    /**
     * select a item
     * @param item
     * @return the selected item
     */
    public Item selectItem(int item) {
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            if (entry.getKey().getName().toLowerCase().equals((EnumItems.values()[item]).toString().toLowerCase())) {
                return (Item) entry.getKey();
            }
        }
        return null;
    }

    /**
     * Decrease the item amount for the selected item
     * @param item selected
     * @param amount to increase
     */
    public void increaseItemAmount(Item item, int amount) {
        items.put(item, amount);
    }

    /**
     * TODO: Handle what happens when ammo is unlimited or out of ammo
     * Increase the item amount for the selected item
     * @param item selected
     * @param amount to decrease
     */
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

    /**
     * Check if item contains an amount
     * @param item
     * @return item
     */
    public int containsItemAmount(Item item) {
        if (items.containsKey(item)) {
            return items.get(item);
        }
        return 0;
    }

    /**
     * 
     * @return string of name and color of team
     */
    @Override
    public String toString() {
        return getName() + " - " + getColor();
    }
}
