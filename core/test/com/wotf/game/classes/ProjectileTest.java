/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import HeadlessRunner.GdxTestRunner;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.extra.Pathfinder;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 *
 * @author Remco
 */
@RunWith(GdxTestRunner.class)
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
        sprite = new Sprite(new Texture(Gdx.files.absolute(Pathfinder.getRelativePath() + "BulletBill.png")));
        ParticleEffect pe = new ParticleEffect();
        projectile = new Projectile(sprite, pe);
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
        Vector2 unitpos = new Vector2(20, 20);
        /**
         * @param mousePos position where the mouse was clicked.
         */
        Vector2 mousepos = new Vector2(20, 20);
        /**
         * @param wind Vector2 wind physics.
         */
        Vector2 windspeed = new Vector2(5, 5);
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
        /**
         * @param damage The damage of the projectile
         */
        int damage = 20;

        projectile.getPosition();
        // Can't be tested because it has no getter for the values this method changed.
        projectile.fire(unitpos, mousepos, force, windspeed, gravity, blastradius, damage);

        // We can't test much but we can test if it moved it's position.
        // It is made to continuesly check for impact with the map. This is very hard to test.
        // 
        assertNotNull(projectile.getPosition());

    }
}
