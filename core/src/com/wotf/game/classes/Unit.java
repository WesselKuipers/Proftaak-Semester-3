package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.wotf.game.GameStage;
import com.wotf.game.classes.Items.Item;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Wessel on 14/03/2016.
 */
public class Unit extends Actor {

    public enum State {
        FALLING, JUMPING, STANDING, RUNNING, DEAD
    };
    public State currentState;
    public State previousState;
    
    private float stateTimer;

    private TextureRegion unitStand;
    private Animation unitRun;
    private TextureRegion unitJump;
    
    private int health;
    private String name;

    public Sprite sprite;
    private Vector2 position;
    public Vector2 velocity;

    private Item weapon;
    private Team team;

    public final float PIXELS_TO_METERS = 100;
    // Font is used for displaying name and health
    private static BitmapFont font = new BitmapFont();

    public Unit(String name, int health, Team team) {
        this.name = name;
        this.health = health;
        this.team = team;
        
        currentState = State.STANDING;
        previousState = State.STANDING;
        
        TextureRegion[] frames;
        frames = new TextureRegion[8];

        Texture spriteSheet = new Texture(Gdx.files.internal("unit.png"));
        TextureRegion[][] tmpFrames = TextureRegion.split(spriteSheet, 80, 120);

        for (int i = 0; i < 8; i++) {
            frames[i] = tmpFrames[0][i];
        }

        Array<TextureRegion> framesRun = new Array<>();

        for (int i = 1; i < 4; i++) {
            framesRun.add(frames[i]);
        }

        unitRun = new Animation(0.1f, framesRun);
        unitStand = frames[0];
        unitJump = frames[2];

        font.setColor(Color.BLACK);

        sprite = new Sprite(unitStand);
        sprite.setRegion(unitStand);

        this.setBounds(getX(), getY(), sprite.getWidth(), sprite.getHeight());
        this.setWidth(sprite.getWidth());
        this.setHeight(sprite.getHeight());

        // Temporary input listener
        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
//                if (keycode == Keys.RIGHT) {
//                    //move(new Vector2(50f, 0));
//                     //b2body.applyLinearImpulse(new Vector2(0.1f, 0), b2body.getLocalCenter(), true);
//                     b2body.setLinearVelocity(0.5f, 0f);
//                }
//
//                if (keycode == Keys.LEFT) {
//                    //move(new Vector2(-50f, 0));
//                    //b2body.applyLinearImpulse(new Vector2(-0.1f, 0), b2body.getLocalCenter(), true);
//                    b2body.setLinearVelocity(-0.5f,0f);
//                }

                if (keycode == Keys.UP) {
                    //move(new Vector2(0, 50f));
                    jump();
                }

                //switching between weapons
                if (keycode == Keys.NUM_1) {
                    // TODO: switch weapon A
                    // In Team's weaponlist choose weapon cooresponding to numbers & make weapon activated
                      Item i = team.selectItem(0);
                    if (i != null) {
                        weapon = i;
                        //TODO: WHAT BUTTON PRESSED TO FIRE WEAPON???????
                        // Wessel: Probably the spacebar button, like in worms
                        //FIRE & USE LOGIC + initiate projectile funtion --> TODO JIP z'n PROJECTIEL FUNCTIE
                        useItem();
                    
                    System.out.println("GRENADE");
                    } else {
                        System.out.println("Selected weapon not found");  
                    }
                }
                
                if (keycode == Keys.NUM_2) {
                     // TODO: switch weapon B
                    // In Team's weaponlist choose weapon cooresponding to numbers
                    // make weapon activated
                    Item i = team.selectItem(1);
                    if (i != null) {
                        weapon = i;
                        //TODO: WHAT BUTTON PRESSED TO FIRE WEAPON???????
                        //FIRE & USE LOGIC + initiate projectile funtion --> TODO JIP z'n PROJECTIEL FUNCTIE
                        useItem();
                        
                    System.out.println("BAZOOKA");                    
                    } else {
                        System.out.println("Selected weapon not found");  
                    }
                }
                
                if (keycode == Keys.SPACE) {
                    // TODO: Logic for firing weapon
                    // should be moved when shooting logic requires more than a single button down event
                    System.out.println("Firing " + weapon.getName());
                    useItem();
                }
                
                return true;
            }
        });
    }

    public Unit(String name, int health, Team team, Vector2 position) {
        this(name, health, team);
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

    public Rectangle getBounds() {
        return this.sprite.getBoundingRectangle();
    }

    public void spawn(Vector2 position) {
        // logic for spawning
        this.setPosition(position.x, position.y);
        this.position = position;
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

    public void jump() {
    }


    public void setPosition(Vector2 position) {
        this.position = position;
    }
    
    public void useItem(){
        //TODO: Jip projectiel functie koppelen aan impact en daarmee de activate aanroepen
        //wapens ontploffen nu bij activate (suicide bombers)
        weapon.activate();
        team.decreaseItemAmount(weapon, 1);
    }
}
