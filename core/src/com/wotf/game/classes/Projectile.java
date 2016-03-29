/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 *
 * @author  Jip Boesenkool
 * @Date    29-03-'16
 */
public class Projectile extends Actor {
    
    private Sprite sprite;

    //projectile trajectory variables
    private float angle;
    private Vector2 velocity;
    private Vector2 acceleration;
    private float totalTime;
    
    //position
    private Vector2 position;
    
    /**
     * Projectile object which handles the logic to fire a projectile.
     * @param unitPos   Start position of the bullet.
     * @param mouseX    Mouse position x.
     * @param mouseY    Mouse position y.
     * @param force     The speed at which the bullet is fired.
     * @param wind      Vector2 which has the force of the wind.
     * @param gravity   Downwards pulling force.
     */
    public Projectile( Vector2 unitPos, int mouseX, int mouseY,
            float force, Vector2 wind, double gravity)
    {                
        //graphics
        sprite = new Sprite(new Texture( Gdx.files.internal("BulletBill.png") ));
        sprite.setOriginCenter();
        sprite.flip(true, false);
        
        this.setBounds(getX(), getY(), sprite.getWidth(), sprite.getHeight());
        this.setWidth(sprite.getWidth());
        this.setHeight(sprite.getHeight());
        
        //game data
        position = unitPos;
        
        //calculate angle
        setAngle( unitPos, new Vector2( mouseX, mouseY ) );
        
        //create velocity (Angle + force)
        setVelocity( force );
        
        //create acceleration from wind resistance and gravity
        setAcceleration( wind, gravity );
    }
    
    /**
     * Calculate the velocity in x and y coordinates by angle.
     * @param force     Force towards direction.
     */
    private void setVelocity( float force ) {
        final double DEG2RAD = Math.PI/180;
        double ang = angle * DEG2RAD;
        
        velocity = new Vector2(
                (float) (force * Math.cos(ang)), 
                (float) (force * Math.sin(ang))
        );
    }
    
    /**
     * Calculate and set the angle by 2 vectors.
     * @param startPos first x,y position.
     * @param destPos  second x, y position.
     */
    private void setAngle( Vector2 startPos, Vector2 destPos ){
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
     * @param wind      Vector2 which has the force of the wind.
     * @param gravity   Downwards pulling force.
     */
    private void setAcceleration( Vector2 wind, double gravity ) {
        //account for wind
        acceleration = new Vector2( wind.x, wind.y );
        
        //account for gravity
        acceleration.y -= gravity;
    }
    
    /**
     * Function which calculates the new position of the object by acceleration.
     */
    public void updateShot( ){
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
    }
    
    
    @Override
    public void positionChanged() {
        sprite.setPosition( getX(), getY() );
        super.positionChanged();
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
    }
    
    @Override
    public void act(float delta) {
        updateShot( );
        super.act(delta);
    }
}
