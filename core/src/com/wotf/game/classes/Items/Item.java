package com.wotf.game.classes.Items;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.wotf.game.GameStage;
import com.wotf.game.classes.Projectile;

/**
 * Abstract class that describes all necessary fields for an Item
 */
public abstract class Item extends Actor {

    /*
    NOTES: create weapons and items accordingly
    Weapons extend item and implement e.g. IExplosion \/ IReplace \/ IHeal \/ IHeal \/ ICluster
    weapons/items have own implementation of these interfaces 
    
    Weapons logic should be remodeled in the next version when it is mapped where what function goes
     */
    private String name;
//    private float power;
//    private int blastRadius;
    // private Sprite weaponSprite;

//    private Projectile bullet;
//    private int damage;
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
    public Item(String nm /*, float pw, int rad, int damage, Sprite weaponSprite, Sprite bulletSprite*/) {
        //graphics
        // this.weaponSprite = weaponSprite;
        this.name = nm;

        /*
        this.power = pw;
        this.blastRadius = rad;
        this.damage = damage;
        this.bullet = new Projectile(bulletSprite);*/
    }

    public void InitItemChildSuper(Sprite weapon_Sprite) {
        this.setBounds(getX(), getY(), weapon_Sprite.getWidth(), weapon_Sprite.getHeight());
        this.setWidth(weapon_Sprite.getWidth());
        this.setHeight(weapon_Sprite.getHeight());
    }

    /**
     * Constructor of Item used to initialize all necessary fields
     *
     * @param nm Name of the item
     * @param pw Power of the item
     * @param weaponSprite Sprite of the weapon of the item
     * @param bulletSprite Sprite of the bullet/projectile of the item
     */
    /* public Item(String nm, float pw, Sprite weaponSprite, Sprite bulletSprite) {
        this(nm, pw, 1, 25, weaponSprite, bulletSprite);
    }
     */
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
