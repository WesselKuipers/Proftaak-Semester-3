package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.wotf.game.classes.Items.Item;
import com.wotf.game.GameStage;
import static com.wotf.game.classes.GameSettings.WEAPONS_ARMORY;

/**
 * Created by Wessel on 14/03/2016.
 */
public class Unit extends Group {
    private float angle;
    public float force = 3f;
    private Vector2 acceleration;
    private TextureRegion unitStand;

    public float speed = 50 * 2;
    public Vector2 velocity = new Vector2();
    boolean moveRight;

    private int health;
    private String name;

    public Sprite sprite;
    private Vector2 position;

    private Item weapon;
    private Team team;

    // Font is used for displaying name and health
    private static BitmapFont font = new BitmapFont();

    public Unit(String name, int health, Team team) {
        this.name = name;
        this.health = health;
        this.team = team;
        moveRight = true;

        Texture spriteSheet = new Texture(Gdx.files.internal("unit.png"));
        
        font.setColor(Color.BLACK);

        sprite = new Sprite(spriteSheet);
        unitStand = sprite;
        //sprite.setRegion(spriteSheet);

        sprite = new Sprite(unitStand);
        sprite.setRegion(unitStand);
        this.setBounds(getX(), getY(), sprite.getWidth(), sprite.getHeight());
        this.setWidth(sprite.getWidth());
        this.setHeight(sprite.getHeight());

        // Temporary input listener
        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.RIGHT) {
                    moveRight = true;
                }

                if (keycode == Keys.LEFT) {
                    moveRight = false;
                }
                if (keycode == Keys.UP) {
                    jump();
                }

                //switching between weapons
                if (keycode == Keys.NUM_1) {
                    // In Team's weaponlist choose weapon cooresponding to numbers & make weapon activated

                    if (team.selectItem(WEAPONS_ARMORY.get(0))) {
                        Item w = WEAPONS_ARMORY.get(0);
                        selectWeapon(w);

                        System.out.println("BAZOOKA");
                    } else {
                        System.out.println("Selected weapon not found");
                    }
                }

                if (keycode == Keys.NUM_2) {
                    // In Team's weaponlist choose weapon cooresponding to numbers & make weapon activated
                    if (team.selectItem(WEAPONS_ARMORY.get(1))) {
                        Item w = WEAPONS_ARMORY.get(1);
                        selectWeapon(w);

                        System.out.println("GRENADE");
                    } else {
                        System.out.println("Selected weapon not found");
                    }
                }

                return true;
            }
        });
    }
    
    //constructor
    public Unit(String name, int health, Team team, Vector2 position) {
        this(name, health, team);
        this.position = position;
        this.velocity = new Vector2(0, 0);
    }

    /**
     * selects a given weapon and adds it to the stage 
     * @param i item object that you want to select
     */
    public void selectWeapon(Item i) {
        destroyWeapon();
        weapon = i;
        //i.initActor();
        ((GameStage) this.getStage()).addActor(weapon);
    }
    
    /**
     * Destroy a previously created weapon
     */
    public void destroyWeapon(){
        if(weapon != null){
            weapon.destroyActor();
        }
    }
    
    /**
     * @return health of the unit
     */
    public int getHealth() {
        return health;
    }

     /**
     * Increase the health of the unit
     * @param amount to increase health
     */
    public void increaseHealth(int amount) {
        health += amount;
    }
    
     /**
     * Decrease the health of the unit
     * @param amount to decrease health
     */
    public void decreaseHealth(int amount) {
        health -= amount;
        if(health < 0) { health = 0; }
    }
    
    /**
     * @return the sprite of the unit
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Set the sprite of this unit
     * @param sprite sorite to be set to the unit
     */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    /**
     * @return position of the unit
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Returns the name associated with this unit
     * @return String containing the name of this unit
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a rectangle representing the bounds of unit
     * @return Rectangle based on X, Y, Width and Height of unit
     */
    public Rectangle getBounds() {
        return this.sprite.getBoundingRectangle();
    }

    /**
     * Spawns a unit at the specified location
     * @param position Position to spawn the unit at
     */
    public void spawn(Vector2 position) {
        // logic for spawning
        this.setPosition(position.x, position.y);
        this.position = position;
        positionChanged();
    }

    /**
     * change position of the unit and update the old with the new position
     */
    @Override
    public void positionChanged() {
        sprite.setPosition(getX(), getY());
        super.positionChanged();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
        
        // Sets color of the font to the same colour of the team
        font.setColor(team.getColor());

        // Draws the name and current health of the unit above its sprite
        font.draw(batch, String.format("%s (%d)", name, health), getX(), getY() + getHeight() + 20);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        sprite.setRegion(getFrame(delta));
        updateJump();
    }

    /**
     * In jump() we set the followin
     *  - setAngle with the next position
     *  - setAcceleration with the gravity
     *  - setVelocity with the force
     * 
     * Then the act calls the updateJump().
     */
    public void jump() {
        System.out.println("V= " + velocity.x);
        float nextX;

        if (moveRight) {
            nextX = position.x + 20;
        } else {
            nextX = position.x - 20;
        }

        setAngle(position, new Vector2(nextX, position.y + 20));
        setAcceleration(9.8);
        setVelocity(force);
    }

    /**
     * updateJump is called in the act() to update the jump of the unit
     * Everytime its called, the position of the unit is update by the velocity
     * and the velocity is calculate by acceleration multiple by delta
     * After that the unit position iss changed.
     * 
     * After changing the position we look for a solid point on the map. 
     * Is it possible the unit can move to the point?
     */
    public void updateJump() {
        if (acceleration == null) {
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();
        
        //keep old position to change rotation of object
        Vector2 oldPos = position.cpy();

        position.x += velocity.x;
        position.y += velocity.y;

        setAngle(oldPos, position);

        velocity.x += acceleration.x * delta;
        velocity.y += acceleration.y * delta;

        //System.out.println(velocity.toString());
        this.setPosition(position.x, position.y);
        positionChanged();
        
        if(!moveRight){
            boolean isSolidX = ((GameStage) getStage()).isPixelSolid((int) position.x - 1, (int) position.y);
            if (isSolidX) {
                velocity.x = 0;
            } 
        }else{
            boolean isSolidX = ((GameStage) getStage()).isPixelSolid((int) position.x + 15, (int) position.y);
            if (isSolidX) {
                velocity.x = 0;
            } 
        }

        boolean isSolidY = ((GameStage) getStage()).isPixelSolid((int) position.x, (int) position.y - 1);
        if (isSolidY) {
            velocity.y = 0;
        }
        sprite.setRotation(angle);
    }

    /**
     * Calculate and set the angle by 2 vectors.
     * @param startPos first x,y position.
     * @param destPos second x, y position.
     */
    private void setAngle(Vector2 startPos, Vector2 destPos) {
        angle = (float) Math.toDegrees(
                Math.atan2(
                        destPos.y - startPos.y,
                        destPos.x - startPos.x
                )
        );
    }
    
    /**
     * Calculate the acceleration per/turn by external forces.
     * @param gravity Downwards pulling force.
     */
    private void setAcceleration(double gravity) {
        acceleration = new Vector2();
        //account for gravity
        acceleration.y -= gravity;
    }
    
    /**
     * Calculate the velocity in x and y coordinates by angle.
     * @param force Force towards direction.
     */
    private void setVelocity(float force) {
        final double DEG2RAD = Math.PI / 180;
        double ang = angle * DEG2RAD;

        velocity = new Vector2(
                (float) (force * Math.cos(ang)),
                (float) (force * Math.sin(ang))
        );
    }
    /**
     * Gets the frame the unit is in. For example running left or running right.
     * @param dt is the delta time
     * @return the region of the sprite.
     */
    public TextureRegion getFrame(float dt) {
        TextureRegion region;
        region = unitStand;
        //if unit is turn left and the texture isnt facing left... flip it.
        if (!moveRight && !region.isFlipX()) {
            region.flip(true, false);
            moveRight = false;
        } //if unit is turn right and the texture isnt facing right... flip it.
        else if (moveRight && region.isFlipX()) {
            region.flip(true, false);
            moveRight = true;
        }
        return region;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
    
    /**
     * Function to fire the active weapon.
     * @param mousePos Position where the mouse was clicked.
     * @param wind     Wind force.
     * @param gravity  Downwards pulling force.
     */
    public void fire( Vector2 mousePos, Vector2 wind, double gravity ) {
        weapon.activate( this.position, mousePos, wind, gravity );
    }
    
    /**
     * Function to get the current weapon of the unit, needed to get bullet in gamescene.
     * @return return activeWeapon.
     */
    public Item getWeapon(){
        return weapon;
    }
}