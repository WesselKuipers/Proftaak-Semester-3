package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.wotf.game.classes.Items.Item;
import com.wotf.game.GameStage;
import static com.wotf.game.classes.GameSettings.WEAPONS_ARMORY;
import java.util.Iterator;

/**
 * Team contains data that represent a team Contains a list of players, list of
 * units, a list of items including ammo, a name and a team colour
 */
public class Team {

    private String name;
    private Color color;
    private final List<Player> players;
    private final List<Unit> units;
    private final Map<Item, Integer> items; // The integer represents the ammo remaining
    private Unit activeUnit;

    /**
     * Constructor of Team, Initialize lists and set active unit index to zero.
     *
     * @param name
     * @param color
     */
    public Team(String name, Color color) {
        items = new HashMap<>();
        items.put(WEAPONS_ARMORY.get(0), 99);

        this.name = name;
        this.color = color;

        //Instantiating list of items
        players = new ArrayList<>();
        units = new ArrayList<>();
    }

    /**
     * Constructor without any graphics Made for the unit testing.
     */
    public Team(String name, Color color, boolean any) {
        items = null;

        this.name = name;
        this.color = color;

        //Instantiating list of items
        players = new ArrayList<>();
        units = new ArrayList<>();
    }

    /**
     * @return all the players of the team
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Adds a player to the team
     *
     * @param p player
     */
    public void addPlayer(Player p) {
        players.add(p);
    }

    /**
     * TODO: Logic for kicking player out Removes a player from the team
     *
     * @param p player
     */
    public void removePlayer(Player p) {
        players.remove(p);
        // TODO: Logic for kicking player out
    }

    /**
     * @return the team name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the team
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return color of team
     */
    public Color getColor() {
        return color;
    }

    /**
     * set the color of team
     *
     * @param color color of team
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return all the units of team
     */
    public List<Unit> getUnits() {
        return Collections.unmodifiableList(units);
    }

    /**
     * @return active unit by active unit index
     */
    public Unit getActiveUnit() {
        return activeUnit;
    }

    /**
     * @param index index of player
     * @return unit by index
     */
    public Unit getUnit(int index) {
        return units.get(index);
    }

    /**
     * Add a unit to the team
     *
     * @param name name of the unit
     * @param health health of the unit
     */
    public void addUnit(String name, int health) {
        units.add(new Unit(name, health, this));
    }

    /**
     * FOR TESTING. Use WITHOUT the boolean.
     */
    public void addUnit(String name, int health, Vector2 position, boolean any) {
        units.add(new Unit(name, health, this, position, true));
    }

    /**
     * When unit is killed (health is zero or lower), remove the actor and unit
     * from team
     *
     * @param unit to be removed
     */
    public void removeUnit(Unit unit) {
        if (unit != null && units.contains(unit)) {
            GameStage gameStage = (GameStage) unit.getStage();
            for (Actor actor : gameStage.getActors()) {
                if (actor == unit) {
                    actor.remove();
                    units.remove(unit);
                }
            }
        }
    }

    /**
     * FOR TESTING. Use WITHOUT the boolean.
     */
    public void removeUnit(Unit unit, boolean any) {
        if (unit != null && units.contains(unit)) {
            units.remove(unit);
        }
    }
    
    /**
     * Begin turn for team
     * If its the first time for the team and there's no active unit set it to the first
     * After that get next active unit
     */
    public void beginTurn() {
        if (activeUnit == null) {
            activeUnit = units.get(0);
        }
        setNextActiveUnit();
    }

    /**
     * end turn for team
     * When unit has lower or equal than 0 health, remove the unit from the team
     */
    public void endTurn() {
        List<Unit> unitsToRemove = new ArrayList<>();
        for (Unit unit : units) {
            if (unit.getHealth() <= 0) {
                unitsToRemove.add(unit);
            }
        }
        for (int i = 0; i < unitsToRemove.size(); i++) {
            removeUnit(unitsToRemove.get(i));
        }
    }
    
    /**
     * Set the next active unit in the team
     */
    public void setNextActiveUnit() {
        int activeUnitIndex = units.indexOf(activeUnit);
        
        // Change the active unit index if its not at the end of the list
        if (activeUnitIndex < (units.size() - 1)) {
            activeUnitIndex++;
        } else {
            activeUnitIndex = 0;
        }
        activeUnit = units.get(activeUnitIndex);
    }

    /**
     * Set active unit
     * @param unit unit
     */
    public void setActiveUnit(Unit unit) {
        this.activeUnit = unit;
    }

    /**
     * select an item that is foudn inside the teamlist
     *
     * @param item item that needs to be selected
     * @return the selected item
     */
    public boolean selectItem(Item item) {
        return containsKey(item) != null;
    }

    /**
     * Check if the item excists inside the team's item list
     *
     * @param item item to check for
     * @return return the item that is found inside the team
     */
    public Item containsKey(Item item) {
        Item result = null;
        for (Entry<Item, Integer> entry : items.entrySet()) {
            if ((entry.getKey().getName().equals(item.getName()))) {
                result = entry.getKey();
                break;
            }
        }
        return result;
    }

    /**
     * Decrease the item amount for the selected item
     *
     * @param item selected
     * @param amount to increase
     */
    public void increaseItemAmount(Item item, int amount) {
        items.put(item, amount);
    }

    /**
     * TODO: Handle what happens when ammo is unlimited or out of ammo Increase
     * the item amount for the selected item
     *
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
     *
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
     * @return string of name and color of team
     */
    @Override
    public String toString() {
        return getName() + " - " + getColor();
    }
}
