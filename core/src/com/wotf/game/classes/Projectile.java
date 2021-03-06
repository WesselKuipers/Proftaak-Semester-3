package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.extra.Pathfinder;
import com.wotf.game.GameStage;
import com.wotf.game.Networking.Command;
import com.wotf.game.Networking.NetworkMessage;

/**
 *
 * @author Jip Boesenkool
 * @Date 29-03-'16
 * Class which holds all the data and functionality to display
 * an bullet and update the logic.
 */
public class Projectile extends Actor {

    private final Sprite sprite;

    //projectile trajectory variables
    private float angle;
    private Vector2 velocity;
    private Vector2 acceleration;
    private float totalTime;

    //position
    private Vector2 position;

    //visual effects
    /**
     * Particle effect associated with this projectile
     */
    public ParticleEffect p;
    
    /**
     * Determines whether or not this projectile is a cluster
     */
    public boolean isCluster = false;
 
    //projectile information
    private int blastRadius;
    private int damage;

    private boolean isExploded;
    
    /**
     * 
     * Projectile constructor to initialize visual appearence of the bullet.
     * 
     * @param sprite Bullet sprite
     * @param effect effect of the particle explosion
     */
    public Projectile(Sprite sprite, ParticleEffect effect) {
        //graphics
        this.sprite = sprite;
        sprite = new Sprite(new Texture(Gdx.files.absolute(Pathfinder.getRelativePath() + "BulletBill.png")));
        sprite.setOriginCenter();
        sprite.flip(true, false);

        this.p = effect;

        this.setBounds(getX(), getY(), sprite.getWidth(), sprite.getHeight());
        this.setWidth(sprite.getWidth());
        this.setHeight(sprite.getHeight());
        
        this.isExploded = false;
    }

    /**
     * Function that handles the shooting of the bullet with physics.
     *
     * @param unitPos Start position from which the bullet is fired from.
     * @param mousePos position where the mouse was clicked.
     * @param force Force of the projectile in the direction of mousePos.
     * @param wind Vector2 wind physics.
     * @param gravity Pulling force towards the ground.
     * @param blastRadius Impact radius of the bullet on the terrain.
     * @param damage Damage this projectile should do should it collide with a
     * unit.
     */
    public void fire(Vector2 unitPos, Vector2 mousePos,
            float force, Vector2 wind, double gravity, int blastRadius, int damage) {

        //game data
        position = unitPos.cpy();

        //projectile information
        this.blastRadius = blastRadius;
        this.damage = damage;

        //calculate angle
        setAngle(unitPos, mousePos);

        //create velocity (Angle + force)
        setVelocity(force);

        //create acceleration from wind resistance and gravity
        setAcceleration(wind, gravity);

        //start updateLoop
        updateShot();
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

        sprite.setRotation(angle);
    }

    /**
     * Calculate the acceleration per/turn by external forces.
     *
     * @param wind Vector2 which has the force of the wind.
     * @param gravity Downwards pulling force.
     */
    private void setAcceleration(Vector2 wind, double gravity) {
        //account for wind
        acceleration = new Vector2(wind.x, wind.y);

        //account for gravity
        acceleration.y -= gravity;
    }

    /**
     * Function which calculates the new position of the object by acceleration.
     */
    public void updateShot() {
        float delta = Gdx.graphics.getDeltaTime();

        //keep old position to change rotation of object
        Vector2 oldPos = position.cpy();

        position.x += velocity.x;
        position.y += velocity.y;

        setAngle(oldPos, position);

        velocity.x += acceleration.x * delta;
        velocity.y += acceleration.y * delta;

        this.setPosition(position.x, position.y);
        positionChanged();
    }

    /**
     * LibGDX function which handles the sprite position.
     */
    @Override
    public void positionChanged() {
        sprite.setPosition(getX(), getY());
        super.positionChanged();
    }

    /**
     * LibGDX function which is responsible for writing the sprite.
     *
     * @param batch ?
     * @param parentAlpha ?
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
    }

    /**
     * LibGDX function which handles the update loop for this object.
     *
     * @param delta Time passed since last update.
     */
    @Override
    public void act(float delta) {
        updateShot();
        super.act(delta);

        Map gameMap = ((GameStage) getStage()).getGame().getMap();
        boolean[][] terrain = gameMap.getTerrain();

        // if projectile is out of bounds, remove it from the stage
        if (isProjectileOutOfBounds(gameMap)) {
            ((GameStage) getStage()).getGame().getTurnLogic().endTurn();
            this.remove();
            isExploded = false;
            return;
        }

        // bounds check for terrain[][]
        if (!(this.getX() >= 0 && this.getY() >= 0 && this.getX() < terrain.length && this.getY() < terrain[0].length)) {
            return;
        }

        // checking collision with units
        ((GameStage) getStage()).isCollidedWithUnit((int) getX(), (int) getY());

        // checking collision with terrain
        terrainCollision();
    }

    /**
     * Function to check if bullet has collided with the terrain.
     *
     * @param terrain Nested boolean array [x][y] which determines if pixel is
     * activated or not.
     */
    private void terrainCollision() {
        // Terrain and unit collision
        if (((GameStage) getStage()).getGame().getPlayingPlayer().getId()== ((GameStage) getStage()).getGame().getActiveTeam().getPlayer().getId() && 
            ((GameStage) getStage()).getGame().getMap().isPixelSolid((int) getX(), (int) getY()) &&
                isExploded == false) {

            NetworkMessage syncCollisionMsg = new NetworkMessage(Command.SYNCCOLLISION);
            
            syncCollisionMsg.addParameter( "posX", Integer.toString( (int) getX() ));
            syncCollisionMsg.addParameter( "posY", Integer.toString( (int) getY() ));
                    
            //send message to host
            ((GameStage) getStage()).getNetworkingUtil().sendToHost( syncCollisionMsg );
            
            isExploded = true;
            
            terrainCollisionReceive((int) getX(), (int) getY());
        }
    }
    
    public void terrainCollisionReceive(int posX, int posY) {
        // Projectile collided with terrain
        System.out.println("Bullet collided at " + posX + " " + posY);
        //((GameStage) getStage()).setParticle(p);
        Gdx.app.postRunnable(() -> {
            ((GameStage) getStage()).explode(posX, posY, blastRadius, damage, isCluster);
            ((GameStage) getStage()).getGame().getTurnLogic().endTurn();
            this.remove();
            isExploded = false;
        });
    }

    /**
     * Function to check if projectile is within bounds.
     *
     * @param gameMap map object which holds data about the map
     * @return True if projectile is out of bounds, false otherwise.
     */
    private boolean isProjectileOutOfBounds(Map gameMap) {
        return this.getX() - this.getWidth() > gameMap.getWidth()
                || this.getX() + this.getWidth() < 0
                || this.getY() + this.getHeight() < 0;
    }
    
    /**
     * Gets the position after firing. This will be used for testing purpose
     * 
     */
    public Vector2 getPosition(){
        return position;
    }
}
