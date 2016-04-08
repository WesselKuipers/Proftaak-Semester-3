/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Remco
 */
public class ProjectileTest {

    private Projectile projectile;
    private Sprite sprite;

    @Before
    public void initProjectile() {

        /**
         * Projectile constructor to initialize visual appearence of the bullet.
         *
         * @param sprite
         */
        sprite = new Sprite(new Texture(Gdx.files.internal("BulletBill.png")));
        projectile = new Projectile(sprite);
    }

    @Test
    public void testInit() {
        // Test if the before class is working properly
        assertNotNull("The before class is not working properly", projectile);
    }

    @Test
    public void testFire() {
        /**
         * Function that handles the shooting of the bullet with physics.
         *
         * @param unitPos Start position from which the bullet is fired from.
         */
        Vector2 unitpos = new Vector2();
        /**
         * @param mousePos position where the mouse was clicked.
         */
        Vector2 mousepos = new Vector2();
        /**
         * @param wind Vector2 wind physics.
         */
        Vector2 windspeed = new Vector2();
        /**
         * @param gravity Pulling force towards the ground.
         */
        double gravity = 10;
        /**
         * @param force Force of the projectile in the direction of mousePos.
         */
        float force = 20;
        /**
         * @param blastRadius Impact radius of the bullet on the terrain.
         */
        int blastradius = 40;

        // Can't be tested because it has no getter for the values this method changed.
        
    }

    @Test
    public void testsetAngle() {
        /**
         * Calculate and set the angle by 2 vectors.
         *
         * @param startPos first x,y position.
         */
        Vector2 startpos = new Vector2(20,60);
        /**
         * @param destPos second x, y position.
         */
        Vector2 destpos = new Vector2(140,80);
        // 9.462322184
        // This should be the result, can't be tested. Sprite in the method which means a crash.
        // Also there is no getter.
        
    }

    @Test
    public void testsetVelocity() {
        /**
         * Calculate the velocity in x and y coordinates by angle.
         *
         * @param force Force towards direction.
         */
        float force = 20;
        //projectile.setVelocity(force);
    }
    
   /* @Test
    public void testupdateShot(){

    }*/
}
