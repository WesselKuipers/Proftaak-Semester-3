package com.wotf.game.classes.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.wotf.game.GameStage;
import com.wotf.game.classes.Projectile;
import java.io.Serializable;

/**
 * Abstract class that describes all necessary fields for an Item
 */
public abstract class Item extends Actor implements Serializable {

    private final float power;
    private final int radius;
    private final int damage;

    public ParticleEffect p;
    public String particleName;

    private Sprite bullet_sprite;
    private Sprite weapon_sprite;

    private Projectile bullet;

    private String name;

    /**
     * Main constructor of Item used to initialize all necessary fields
     *
     * @param nm Name of the item
     * @param pw Power of the item
     * @param rad Radius of the explosion of the item
     * @param damage Damage the item inflicts
     * @param weaponSprite Sprite of the weapon of the item
     * @param bulletSprite Sprite of the bullet/projectile of the item
     * @param pe particle effect that is started with the event trigger
     * 'explosion'
     * @param bullet projectile that will be fired as bullet and triggers event
     * on impact/activation
     */
    public Item(String nm, float pw, int rad, int damage, Sprite weaponSprite, Sprite bulletSprite, String particleEffect) {
        this.name = nm;
        this.power = pw;
        this.radius = rad;
        this.damage = damage;;
        this.bullet = bullet;
        this.bullet_sprite = bulletSprite;
        this.weapon_sprite = weaponSprite;

        this.p = new ParticleEffect();
        p.load(Gdx.files.internal(particleEffect), Gdx.files.internal("effects"));

        InitItemChildSuper(weapon_sprite);
    }

    public void InitItemChildSuper(Sprite weaponSprite) {
        this.setBounds(getX(), getY(), weaponSprite.getWidth(), weaponSprite.getHeight());
        this.setWidth(weaponSprite.getWidth());
        this.setHeight(weaponSprite.getHeight());
    }

    /**
     * @return sprite image of the projectile/bullet
     */
    public abstract Texture getProjectileTexture();

    /**
     * @return sprite image of the projectile/bullet
     */
    public Sprite getProjectileSprite() {
        return this.bullet_sprite;
    }

    /**
     * @return stprite image of the weapon
     */
    public Sprite getWeaponSprite() {
        return this.weapon_sprite;
    }

    /**
     * @return particle effect
     */
    public ParticleEffect getParticle(){
        return this.p;
    }

    /**
     * @return the name of the item
     */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * @return gets the power of the item
     */
    public float getPower(){
        return this.power;
    }

    /**
     * @return gets the initial blastradius
     */
    public int getBlastRadius(){
        return this.radius;
    }

    /**
     * @return gets the bullet
     */
    public Projectile getBullet(){
        return this.bullet;
    }

    /**
     * @return gets the damage that this item can do
     */
    public int getDamage(){
        return this.damage;
    }

    /**
     * initiate this object as an actor
     */
    public void initActor() {
        ((GameStage) this.getStage()).addActor(this); //todo--> Add update sequence in gamestage
    }

    /**
     * destroys the actor object of the item
     */
    public void destroyActor() {
        this.remove();
    }

    /**
     * The implementation of this method should specifiy what an item does when
     * it's activated
     *
     * @param position Position from which the activation originates
     * @param mousePos Mouse position of the activation
     * @param wind Current wind upon activation
     * @param grav Gravity modifier upon activation
     */
    public abstract void activate(Vector2 position, Vector2 mousePos, Vector2 wind, double grav);

}
