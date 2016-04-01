package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
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
    private boolean runningRight;

    public Body b2body;
    public World world;

    private TextureRegion unitStand;
    private Animation unitRun;
    private TextureRegion unitJump;

    private int health;
    private String name;

    public Sprite sprite;
    private Vector2 position;
    private Vector2 velocity;

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
        stateTimer = 0;
        runningRight = true;

        TextureRegion[] frames;
        frames = new TextureRegion[8];

        Texture spriteSheet = new Texture(Gdx.files.internal("unit.png"));
        TextureRegion[][] tmpFrames = TextureRegion.split(spriteSheet, 80, 120);

        for (int i = 0; i < 8; i++) {
            frames[i] = tmpFrames[0][i];
        }

        Array<TextureRegion> framesRun = new Array<TextureRegion>();

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

                return true;
            }

        });
    }

    public Unit(String name, int health, Team team, Vector2 position) {
        this(name, health, team);
        this.position = position;
        this.velocity = new Vector2(0, 0);
    }

//    public void redefineBody(){
//        world.destroyBody(b2body);
//        
//        BodyDef bdef = new BodyDef();
//        bdef.type = BodyDef.BodyType.DynamicBody;
//        bdef.position.set(position.x /PIXELS_TO_METERS - 12.5f, position.y / PIXELS_TO_METERS - 3);
//
//
//        b2body = world.createBody(bdef);
//        
//        CircleShape shape = new CircleShape();
//        shape.setRadius(1f);
//        FixtureDef fdef = new FixtureDef();
//        fdef.shape = shape;
//        
//        b2body.createFixture(fdef);
//        shape.dispose();
//    }
    
    public void defineBody(World world) {
        this.world = world;
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;

        bdef.position.set(position.x / PIXELS_TO_METERS - 6f, position.y / PIXELS_TO_METERS + 1f);

        b2body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.8f);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        b2body.createFixture(fdef);
        shape.dispose();
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

//    public void move(Vector2 direction) {
//        // logic for moving
//        // Temporary movement logic
//        MoveByAction mba = new MoveByAction();
//        mba.setAmount(direction.x, direction.y);
//        mba.setDuration(3f);
//        this.addAction(mba);
//    }
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
        if(b2body != null)
            sprite.setRegion(getFrame(delta));
    }

    public void jump() {
        b2body.applyLinearImpulse(new Vector2(0, 10), b2body.getLocalCenter(), true);
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public TextureRegion getFrame(float dt) {
        //get unit current state. ie. jumping, running, standing...
        currentState = getState();

        TextureRegion region;

        //depending on the state, get corresponding animation keyFrame.
        switch (currentState) {
            case JUMPING:
                region = unitJump;
                break;
            case RUNNING:
                region = unitRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = unitStand;
                break;
        }

        //if unit is running left and the texture isnt facing left... flip it.
        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } //if unit is running right and the texture isnt facing right... flip it.
        else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and we need to reset timer.
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        //update previous state
        previousState = currentState;
        //return our final adjusted frame
        return region;

    }

    public State getState() {
        //if unit is going positive in Y-Axis he is jumping... or if he just jumped and is falling remain in jump state
        
        if ((b2body.getLinearVelocity().y > 0 && currentState == State.JUMPING) || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
            return State.JUMPING;
        } //if negative in Y-Axis unit is falling
        else if (b2body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } //if unit is positive or negative in the X axis he is running
        else if (b2body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } //if none of these return then he must be standing
        else {
            return State.STANDING;
        }
    }
    
    public void useItem(){
        //TODO: Jip projectiel functie koppelen aan impact en daarmee de activate aanroepen
        //wapens ontploffen nu bij activate (suicide bombers)
        weapon.activate();
        team.decreaseItemAmount(weapon, 1);
    }
}
