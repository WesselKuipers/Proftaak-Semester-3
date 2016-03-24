package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;

/**
 * Created by Wessel on 14/03/2016.
 */
public class Unit extends Actor {
    private int health;
    private String name;

    private Sprite sprite;

    private Vector2 position;
    private Vector2 velocity;
    
    // Font is used for displaying name and health
    private static BitmapFont font = new BitmapFont();

    public Unit(String name, int health) {
        this.name = name;
        this.health = health;
        
        font.setColor(Color.BLACK);
        
        // TODO: Replace badlogic.jpg with spritesheet
        sprite = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg")));
        this.setBounds(getX(), getY(), sprite.getWidth(), sprite.getHeight());
        this.setWidth(sprite.getWidth());
        this.setHeight(sprite.getHeight());
        
        // Temporary input listener
        addListener(new InputListener() {
           @Override 
           public boolean keyDown(InputEvent event, int keycode) {
               if(keycode == Keys.RIGHT) {
                   move(new Vector2(50f, 0));
               }
               
               if(keycode == Keys.LEFT) {
                   move(new Vector2(-50f, 0));
               }
               
               if(keycode == Keys.UP) {
                   move(new Vector2(0, 50f));
               }
               
               if(keycode == Keys.DOWN) {
                   move(new Vector2(0, -50f));
               }
               
               return true;
           }
           
        });
    }

    public Unit(String name, int health, Vector2 position) {
        this(name, health);
        this.position = position;
        this.velocity = new Vector2(0, 0);
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
        // Temporary movement logic
        MoveByAction mba = new MoveByAction();
        mba.setAmount(direction.x, direction.y);
        mba.setDuration(3f);
        this.addAction(mba);
    }

    public void spawn(Vector2 position) {
        // logic for spawning
        this.setPosition(position.x, position.y);
        positionChanged();
    }
    
    @Override
    public void positionChanged() {
        sprite.setPosition(getX(), getY());
        super.positionChanged();
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
        
        // Draws the name and current health of the unit above its sprite
        font.draw(batch, String.format("%s (%d)", name, health), getX(), getY() + getHeight() + 20);
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
