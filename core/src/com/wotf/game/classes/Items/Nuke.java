/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.wotf.game.GameStage;
import com.wotf.game.classes.Projectile;

/**
 * @author chaos
 */
public class Nuke extends Item {

    /**
     * {@inheritDoc}
     */
    public Nuke() {
        super("Nuke",
                10f,
                80,
                60,
                new Sprite(new Texture(Gdx.files.internal("remote.png"))),
                new Sprite(new Texture(Gdx.files.internal("nuclearbomb.png"))),
                "effects/nuke_explosion.p"
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate(Vector2 position, Vector2 mousePos, Vector2 Wind, double grav) {
        //spawn bullet and add to scene
        Projectile bullet = super.getBullet();

        //set new fire from position
        Vector2 projectilePosition = position.cpy();
        projectilePosition.y = ((GameStage) this.getStage()).getHeight() + 1;
        projectilePosition.x = mousePos.x;

        bullet.fire(projectilePosition, mousePos, super.getPower(), Vector2.Zero, 8.0, super.getBlastRadius(), super.getDamage());
        bullet.updateShot();
        ((GameStage) this.getStage()).addActor(bullet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Texture getProjectileTexture() {
        return new Texture(Gdx.files.internal("nuclearbomb.png"));
    }
}
