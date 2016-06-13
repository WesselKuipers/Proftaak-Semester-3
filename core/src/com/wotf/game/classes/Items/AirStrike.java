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
import com.badlogic.gdx.utils.Array;
import com.extra.Pathfinder;
import com.wotf.game.GameStage;
import com.wotf.game.classes.Projectile;
import java.util.Random;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author chaos
 */
public class AirStrike extends Item {

    private final float power = 19;
    private final int radius = 30;
    private final int damage = 33;

    public ParticleEffect p;
    public String particleName = "effects/rocket_explosion.p";

    static Texture weaponTexture = new Texture(Gdx.files.absolute(Pathfinder.getRelativePath() + "rocket.png"));
    private Sprite weapon_sprite;

    /**
     * {@inheritDoc}
     */
    public AirStrike(String nm) {
        super(nm);
        p = new ParticleEffect();
        p.load(Gdx.files.absolute(Pathfinder.getRelativePath() + particleName), Gdx.files.absolute(Pathfinder.getRelativePath() + "effects"));

        weapon_sprite = new Sprite(new Texture(Gdx.files.absolute(Pathfinder.getRelativePath() + "remote.png")));

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
        //spawn rockets and add to scene   

        for (int i = 0; i <= 5; i++) {
            Projectile missile = new Projectile(new Sprite(weaponTexture), p);

            Vector2 projectilePosition = position.cpy();
            projectilePosition.x = 0 + (i * 100);
            projectilePosition.y = ((GameStage) this.getStage()).getHeight();

            mousePos.x += (i * 100);
            //fire: fire from, fire towards, power, wind, gravity, radius, damage
            missile.fire(projectilePosition, mousePos, this.power, Vector2.Zero, grav, this.radius, this.damage);
            missile.updateShot();
            ((GameStage) this.getStage()).addActor(missile);
        }
    }

    /**
     * @return particle affect
     */
    @Override
    public ParticleEffect getParticle() {
        return p;
    }
    
    /**
     * @return particle affect
     */
    @Override
      public Texture getProjectileTexture() {
        return new Texture(Gdx.files.absolute(Pathfinder.getRelativePath() + "rocket.png"));
    }

    /**
     * @return stprite image of the weapon
     */
    @Override
    public Sprite getProjectileSprite() {
        return weapon_sprite;
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
        return null;
    }

    /**
     * @return gets the damage that this item can do
     */
    @Override
    public int getDamage() {
        return damage;
    }
}
