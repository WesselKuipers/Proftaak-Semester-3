package com.wotf.game.classes.Items;

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
public abstract class Item extends Actor implements Serializable{

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
     */
    public Item(String nm ) {
        this.name = nm;
    }

    public void InitItemChildSuper(Sprite weapon_Sprite) {
        this.setBounds(getX(), getY(), weapon_Sprite.getWidth(), weapon_Sprite.getHeight());
        this.setWidth(weapon_Sprite.getWidth());
        this.setHeight(weapon_Sprite.getHeight());
    }

    /**
     * @return sprite image of the projectile/bullet
     */
     public abstract Texture getProjectileTexture();
         
    /**
     * @return sprite image of the projectile/bullet
     */
     public abstract Sprite getProjectileSprite();
     
    /**
     * @return stprite image of the weapon
     */
    public abstract Sprite getWeaponSprite();
    
    /**
     * @return particle affect
     */
      public abstract ParticleEffect getParticle();

    /**
     * @return the name of the item
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name sets the name of the item
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return gets the power of the item
     */
    public abstract float getPower();

    /**
     * @return gets the initial blastradius
     */
    public abstract int getBlastRadius();

    /**
     * @return gets the bullet
     */
    public abstract Projectile getBullet();

    /**
     * @return gets the damage that this item can do
     */
    public abstract int getDamage();

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
