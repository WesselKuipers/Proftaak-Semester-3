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
import java.util.Random;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author chaos
 */
public class Nuke extends Item {

    private final float power = 10;
    private final int radius = 80;
    private final int damage = 60;

    public ParticleEffect p;
    public String particleName = "effects/nuke_explosion.p";

    private Sprite bullet_sprite;
    private Sprite weapon_sprite;

    private Projectile bullet;

    /**
     * {@inheritDoc}
     */
    public Nuke(String nm) {
        super(nm);
        p = new ParticleEffect();
        p.load(Gdx.files.internal(particleName), Gdx.files.internal("effects"));

        bullet_sprite = new Sprite(new Texture(Gdx.files.internal("nuclearbomb.png")));
        weapon_sprite = new Sprite(new Texture(Gdx.files.internal("remote.png")));

        this.bullet = new Projectile(bullet_sprite, p);

        super.InitItemChildSuper(weapon_sprite);
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
        //spawn bullet and add to scene
        Projectile bullet = this.bullet;

        //set new fire from position
        Vector2 projectilePosition = position.cpy();          
        projectilePosition.y = ((GameStage) this.getStage()).getHeight()+1;
        projectilePosition.x = mousePos.x;

        bullet.fire(projectilePosition, mousePos, this.power, Vector2.Zero, 8.0, this.radius, this.damage);
        bullet.updateShot();
        ((GameStage) this.getStage()).addActor(bullet);
    }

    /**
     * @return particle affect
     */
    @Override
    public ParticleEffect getParticle() {
        return p;
    }

    /**
     * @return stprite image of the weapon
     */
    @Override
    public Sprite getWeaponSprite() {
        return weapon_sprite;
    }

    /**
     * @return gets the power of the item
     */
    @Override
    public float getPower() {
        return power;
    }

    /**
     * @return gets the initial blastradius
     */
    @Override
    public int getBlastRadius() {
        return radius;
    }

    /**
     * @return gets the bullet
     */
    @Override
    public Projectile getBullet() {
        return bullet;
    }

    /**
     * @return gets the damage that this item can do
     */
    @Override
    public int getDamage() {
        return damage;
    }
}
