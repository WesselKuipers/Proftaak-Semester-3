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
import com.extra.Pathfinder;
import com.wotf.game.GameStage;
import com.wotf.game.classes.Projectile;

/**
 * @author chaos
 */
public class AirStrike extends Item {

    /**
     * {@inheritDoc}
     */
    public AirStrike() {
        super("Airstrike",
                19f,
                30,
                33,
                new Sprite(new Texture(Gdx.files.absolute(Pathfinder.getRelativePath() + "remote.png"))),
                new Sprite(new Texture(Gdx.files.absolute(Pathfinder.getRelativePath() + "rocket.png"))),
                Pathfinder.getRelativePath() + "effects/rocket_explosion.p");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate(Vector2 position, Vector2 mousePos, Vector2 Wind, double grav) {
        for (int i = 0; i <= 5; i++) { //spawn rockets and add to scene  
            Projectile missile = new Projectile(super.getProjectileSprite(), super.getParticle());

            Vector2 projectilePosition = position.cpy();
            projectilePosition.x = 0 + (i * 100);
            projectilePosition.y = ((GameStage) this.getStage()).getHeight();

            mousePos.x += (i * 100);
            //fire: fire from, fire towards, power, wind, gravity, radius, damage
            missile.fire(projectilePosition, mousePos, super.getPower(), Vector2.Zero, grav, super.getBlastRadius(), super.getDamage());
            missile.updateShot();
            ((GameStage) this.getStage()).addActor(missile);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
      public Texture getProjectileTexture() {
        return new Texture(Gdx.files.absolute(Pathfinder.getRelativePath() + "rocket.png"));
    }

}
