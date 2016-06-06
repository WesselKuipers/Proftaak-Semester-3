/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.wotf.game.GameStage;
import com.wotf.game.classes.Projectile;

/**
 * @author chaos
 */
public class Bazooka extends Item {

    /**
     * {@inheritDoc}
     */
    public Bazooka() {
        super("Bazooka", 
                10f,
                30,
                30,
                new Sprite(new Texture(Gdx.files.internal("Bazooka.png"))),
                new Sprite(new Texture(Gdx.files.internal("BulletBill.png"))),
                "effects/rocket_explosion.p"
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate(Vector2 position, Vector2 mousePos, Vector2 Wind, double grav) {
        //spawn bullet and add to scene
        Projectile bullet = super.getBullet();
        bullet.fire(position, mousePos, super.getPower(), Wind, grav, super.getBlastRadius(), super.getDamage());
        bullet.updateShot();
        ((GameStage) this.getStage()).addActor(bullet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Texture getProjectileTexture() {
        return new Texture(Gdx.files.internal("BulletBill.png"));
    }
}
