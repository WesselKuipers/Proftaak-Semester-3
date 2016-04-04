package com.wotf.game.classes.Items;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.wotf.game.GameStage;
import com.wotf.game.classes.Projectile;

/**
 * Created by wesse on 14/03/2016.
 */
public abstract class Item extends Actor {

    /*
    NOTES: create weapons and items accordingly
    Weapons extend item and implement e.g. IExplosion \/ IReplace \/ IHeal \/ IHeal \/ ICluster
    weapons/items have own implementation of these interfaces 
    
     */
    private String name;
    private float power;
    private int blastRadius;
    public Sprite Weaponsprite;
    private Projectile bullet;

    public Item(String nm, float pw, int rad, Sprite weaponSprite, Sprite bulletSprite) {
        this.name = nm;
        this.power = pw;
        this.blastRadius = rad;
        this.Weaponsprite = weaponSprite;
        this.bullet = new Projectile(bulletSprite);
    }

    public Item(String nm, float pw, Sprite weaponSprite, Sprite bulletSprite) {
        this(nm, pw, 1, weaponSprite, bulletSprite);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getBlastRadius() {
        return blastRadius;
    }

    public void setBlastRadius(int blastRadius) {
        this.blastRadius = blastRadius;
    }

    public Projectile getBullet() {
        return bullet;
    }

    public void initActor() {
        ((GameStage) this.getStage()).addActor(this); //todo--> Add update sequence in gamestage
    }

    public void destroyActor() {
        this.remove();
    }

    public abstract void activate(Vector2 position, Vector2 mousePos, Vector2 Wind, double grav, int blastRadius);

}
