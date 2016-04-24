/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game.classes;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 *
 * @author chaos
 */
public class ParticleEffectActor extends Actor {

    ParticleEffect effect;

    public ParticleEffectActor(ParticleEffect effect) {
        this.effect = effect;
    }

    public void draw(SpriteBatch batch, float parentAlpha) {
        effect.draw(batch); //define behavior when stage calls Actor.draw()
    }

    public void act(float delta) {
        super.act(delta);
        effect.update(delta);
        //effect.setPosition(x,y); //--> can't be accesd from here
        effect.start();
    }

    public ParticleEffect getEffect() {
        return effect;
    }
}
