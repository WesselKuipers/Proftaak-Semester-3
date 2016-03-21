package com.wotf.game.classes;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Wessel on 14/03/2016.
 */
public class Unit {
    private int health;
    private String name;

    private Sprite sprite;

    private Vector2 position;
    private Vector2 velocity;

    public Unit(String name, int health) {
        this.name = name;
        this.health = health;
    }

    public Unit(String name, int health, Vector2 position) {
        this(name, health);
        this.position = position;
    }

    public int getHealth() {
        return health;
    }

    public void increaseHealth(int amount) {
        health += amount;
    }

    public void decreaseHealth(int amount) {
        health -= amount;
        // TODO: checking if health <= 0
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public Vector2 getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public void move(Vector2 direction) {
        // logic for moving
    }

    public void spawn(Vector2 position) {
        // logic for spawning
    }
}
