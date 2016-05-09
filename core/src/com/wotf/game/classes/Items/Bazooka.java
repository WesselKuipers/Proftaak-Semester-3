/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes.Items;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * @author chaos
 */
public class Bazooka extends MissileLauncher {

   /* public ParticleEffect p = new ParticleEffect();
    
    Sprite bullet_sprite = new Sprite(new Texture(Gdx.files.internal("BulletBill.png")));
    Sprite bazooka_sprite = new Sprite(new Texture(Gdx.files.internal("Bazooka.png")));*/

    /**
     * {@inheritDoc}
     */
    public Bazooka(String nm, int pw, int rad, int damage, Sprite weaponSprite, Sprite bulletSprite) {
        super(nm, pw, rad, damage, weaponSprite, bulletSprite);
        //p.load(Gdx.files.internal("effects/test_explosion.p"), Gdx.files.internal("effects"));
    }

    /**
     * trigger the activation method of the object
     *
     * @param position position from where it is fired
     * @param mousePos pisition where it fires too
     * @param Wind wind affection
     * @param grav gravity affection
     */
    @Override
    public void activate(Vector2 position, Vector2 mousePos, Vector2 Wind, double grav) {
        super.activate(position, mousePos, Wind, grav);
    }

}
