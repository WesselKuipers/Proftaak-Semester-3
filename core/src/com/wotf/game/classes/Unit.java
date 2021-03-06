package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.extra.Pathfinder;
import com.wotf.game.classes.Items.Item;
import com.wotf.game.GameStage;
import static com.wotf.game.classes.GameSettings.WEAPONS_ARMORY;
import java.io.Serializable;

/**
 * Unit represents a playable character on the map
 */
public class Unit extends Group implements Serializable {

    private float angle;
    private Vector2 acceleration;
    private TextureRegion unitStand;
    private Vector2 velocity = new Vector2();
    private boolean moveRight;
    private int health;
    private String name;
    private Sprite sprite;
    private Vector2 position;
    private Item weapon;
    private Team team;
    // Font is used for displaying name and health
    private BitmapFont font;

    /**
     * Initializes a unit object
     *
     * @param name Name of the unit
     * @param health Amount of health the unit starts with
     * @param team Team this unit belongs to
     */
    public Unit(String name, int health, Team team) {
        this.name = name;
        this.health = health;
        this.team = team;
        font = new BitmapFont();
        moveRight = true;

        Texture spriteSheet = new Texture(Gdx.files.absolute(Pathfinder.getRelativePath() + "unit.png"));

        font.setColor(team.getColor());

        sprite = new Sprite(spriteSheet);
        unitStand = sprite;
        //sprite.setRegion(spriteSheet);

        sprite = new Sprite(unitStand);
        sprite.setRegion(unitStand);
        this.setBounds(getX(), getY(), sprite.getWidth(), sprite.getHeight());
        this.setWidth(sprite.getWidth());
        this.setHeight(sprite.getHeight());
        setAcceleration(9.8);
    }

    /**
     * Initializes a unit object with a starting position
     *
     * @param name Name of the unit
     * @param health Amount of health the unit starts with
     * @param team Team this unit belongs to
     * @param position Position to start the unit at
     */
    public Unit(String name, int health, Team team, Vector2 position) {
        this(name, health, team);
        this.position = position;
        this.velocity = new Vector2(0, 0);
    }

    /**
     * first function in selecting a weapon from the list of weapons
     *
     * @param number number of the weapon that needs to be found
     */
    public void selectWeaponIndex(int number) {
        if (team.selectItem(WEAPONS_ARMORY.get(number))) {
            Item w = WEAPONS_ARMORY.get(number);
            Unit.this.clearChildren();
            Unit.this.selectWeapon(w);
            Image weaponImage = new Image(w.getWeaponSprite());
            weaponImage.setPosition(Unit.this.getX(), Unit.this.getY());
            Unit.this.addActor(weaponImage);
            
        } else {
            System.out.println("Selected weapon not found");
        }
    }

    /**
     * Selects a given weapon and adds it to the stage
     *
     * @param i Item object that you want to select
     */
    public void selectWeapon(Item i) {
        Unit.this.clearChildren();
        destroyWeapon();
        weapon = i;
        //i.initActor();
        ((GameStage) this.getStage()).addActor(weapon);
    }

    /**
     * Destroy a previously created weapon
     */
    public void destroyWeapon() {
        if (weapon != null) {
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
     *
     * @param amount to increase health
     */
    public void increaseHealth(int amount) {
        health += amount;
    }

    /**
     * Decrease the health of the unit
     *
     * @param amount to decrease health
     */
    public void decreaseHealth(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    /**
     * @return the sprite of the unit
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Set the sprite of this unit
     *
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
     *
     * @return String containing the name of this unit
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns a rectangle representing the bounds of unit
     *
     * @return Rectangle based on X, Y, Width and Height of unit
     */
    public Rectangle getBounds() {
        return this.sprite.getBoundingRectangle();
    }

    /**
     * Spawns a unit at the specified location
     *
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

    /**
     * Draws the unit's sprite to the spriteBatch Also draws text representing
     * the unit's health, team colour and name
     *
     * @param batch SpriteBatch to draw to
     * @param parentAlpha Alpha channel to control transparancy
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);

        // Draws the name and current health of the unit above its sprite
        font.draw(batch, String.format("%s (%d)", name, health), getX(), getY() + getHeight() + 20);

        this.drawChildren(batch, parentAlpha);
    }

    /**
     * Performs an update step for this unit
     *
     * @param delta Deltatime since last act() call
     */
    @Override
    public void act(float delta) {
        super.act(delta);
        sprite.setRegion(getFrame(delta));
        updateJump(delta);

        // flashes active unit to white and back to its original colour
        if (((GameStage) this.getStage()).getGame().getActiveTeam().equals(team)) {
            if (((GameStage) this.getStage()).getGame().getTurnLogic().getElapsedTime() % 2 == 1) {
                font.setColor(Color.WHITE);
            } else {
                font.setColor(team.getColor());
            }
        } else {
            font.setColor(team.getColor());
        }
        
        // make weapons move with the unit
        Array<Actor> children = this.getChildren();
        if (children.size > 0) {
            children.first().setPosition(Unit.this.getX(), Unit.this.getY());
        }

        // Units with 0 health automatically get cleaned up at the end of the turn
        // We assume units are dead if they end up out of bounds
        if (isOutOfBounds()) {
            health = 0;

            // if it's currently this unit's turn, manually call the endTurn() method
            if (((GameStage) this.getStage()).getGame().getActiveTeam().getActiveUnit().equals(this)) {
                ((GameStage) this.getStage()).getGame().getTurnLogic().endTurn();
            }
        }
    }

    /**
     * In jump() we set the following - setAngle with the next position -
     * setAcceleration with the gravity - setVelocity with the force
     *
     * Then the act calls the updateJump().
     *
     * @param moveRight
     */
    public void jump(boolean moveRight) {
        this.moveRight = moveRight;
        // Jumping once
        if (velocity.x != 0 && velocity.y != 0) {
            return;
        }
        Vector2 nextPos;

        if (moveRight) {
            nextPos = new Vector2(position.x + 20, position.y + 20);
            if (((GameStage) getStage()).getGame().getMap()
                    .isPixelSolid((int) nextPos.x + 5, (int) position.y + 3)) {
                return;
            }
        } else {
            nextPos = new Vector2(position.x - 20, position.y + 20);
            if (((GameStage) getStage()).getGame().getMap()
                    .isPixelSolid((int) nextPos.x + 5, (int) position.y + 3))
                return;
        }

        setAngle(position, nextPos);
        setVelocity(3f);
    }

    /**
     * updateJump is called in the act() to update the jump of the unit
     * Everytime its called, the position of the unit is update by the velocity
     * and the velocity is calculate by acceleration multiple by delta After
     * that the unit position iss changed.
     *
     * After changing the position we look for a solid point on the map. Is it
     * possible the unit can move to the point?
     *
     * @param delta
     */
    public void updateJump(float delta) {
        //keep old position to change rotation of object
        Vector2 oldPos = position.cpy();

        position.x += velocity.x;
        position.y += velocity.y;

        setAngle(oldPos, position);

        velocity.x += acceleration.x * delta;
        velocity.y += acceleration.y * delta;

        checkSolid();
        this.setPosition(position.x, position.y);
        positionChanged();
        //sprite.setRotation(angle);
    }

    /**
     * Checks if the next position is solid based on velocity
     */
    public void checkSolid() {

        if (velocity.y >= -2) {
            if (((GameStage) getStage()).getGame().getMap()
                    .isPixelSolid((int) position.x, (int) position.y)
                    || ((GameStage) getStage()).getGame().getMap()
                    .isPixelSolid((int) position.x - 1, (int) position.y)
                    || ((GameStage) getStage()).getGame().getMap()
                    .isPixelSolid((int) position.x + 16, (int) position.y)) {
                velocity = new Vector2();
            }
        } else if (((GameStage) getStage()).getGame().getMap()
                .isPixelSolid((int) position.x, (int) position.y - 10)
                || ((GameStage) getStage()).getGame().getMap()
                .isPixelSolid((int) position.x - 1, (int) position.y)
                || ((GameStage) getStage()).getGame().getMap()
                .isPixelSolid((int) position.x + 16, (int) position.y)) {
            velocity = new Vector2();
        }
    }

    /**
     * Calculate and set the angle by 2 vectors.
     *
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
     * Calculate the acceleration per/turn.
     *
     * @param gravity Downwards pulling.
     */
    private void setAcceleration(double gravity) {
        acceleration = new Vector2();
        //account for gravity
        acceleration.y -= gravity;
    }

    /**
     * Calculate the velocity in x and y coordinates by angle.
     *
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
     *
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

    /**
     * Sets the unit's position to a new position
     *
     * @param position Position to set
     */
    public void setPosition(Vector2 position) {
        this.position = position;
    }

    /**
     * Function to fire the active weapon.
     *
     * @param mousePos Position where the mouse was clicked.
     * @param wind Wind force.
     * @param gravity Downwards pulling force.
     */
    public void fire(Vector2 mousePos, Vector2 wind, double gravity) {
        weapon.activate(this.position, mousePos, wind, gravity);
        destroyWeapon();
    }

    /**
     * Function to get the current weapon of the unit, needed to get bullet in
     * gamescene.
     *
     * @return return activeWeapon.
     */
    public Item getWeapon() {
        return weapon;
    }

    /**
     * Checks if the unit (checking from the center of the sprite) is currently
     * out of bounds
     *
     * @return True if out of bounds, false if not
     */
    public boolean isOutOfBounds() {
        Rectangle bounds = ((GameStage) this.getStage()).getGame().getMap().getBounds();
        return !bounds.contains(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2);
    }
}
