/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes.Items;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.wotf.game.GameStage;
import com.wotf.game.classes.Projectile;

/**
 *
 * @author chaos
 */
public class MissileLauncher extends Item {

    public MissileLauncher(String nm, int pw, int rad, int damage, Sprite weaponSprite, Sprite bulletSprite) {
        super(nm, pw, rad, damage, weaponSprite, bulletSprite);
    }

    /**
     * The activate function that is given from Item and will be given to specific weapons
     * @param position original fire position
     * @param mousePos position to fire too
     * @param Wind wind affection
     * @param grav gravity affection
     */
    @Override
    public void activate(Vector2 position, Vector2 mousePos, Vector2 Wind, double grav) {
        //spawn bullet and add to scene
        Projectile bullet = super.getBullet();
        bullet.fire(position, mousePos, super.getPower(), Wind, grav, super.getBlastRadius(), super.getDamage());
        bullet.updateShot();
        ((GameStage) this.getStage()).addActor(bullet);
    }

   }
