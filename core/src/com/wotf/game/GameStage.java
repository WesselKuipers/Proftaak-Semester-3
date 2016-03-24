/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.wotf.game.classes.Game;
import com.wotf.game.classes.Team;
import com.wotf.game.classes.Unit;

/**
 *
 * @author Wessel
 */
public class GameStage extends Stage {

    Game game;

    SpriteBatch batch;
    BitmapFont font;

    public GameStage(Game game) {
        super(new ScreenViewport());
        this.game = game;

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
    }

    public void init() {
        // Adds every unit as an actor to this stage
        for (Team team : game.getTeams()) {
            for (Unit unit : team.getUnits()) {
                // Retrieves the currently attached sprite of this unit,
                // adds a color tint to it and assigns it back to the unit
                Sprite unitSprite = unit.getSprite();
                unitSprite.setColor(team.getColor());
                unit.setSprite(unitSprite);

                // Spawns a unit in a random location (X axis)
                unit.spawn(new Vector2(MathUtils.random(0, 1280 - 256), 50));

                this.addActor(unit);
            }
        }
    }

    @Override
    public void draw() {
        super.draw();

        batch.begin();
        font.draw(batch, "Debug variables:", 0, this.getHeight());
        font.draw(batch, "Actors amount: " + this.getActors().size, 0, this.getHeight() - 20);
        font.draw(batch, 
                String.format("Active actor: %s XY[%f, %f]",
                              this.getKeyboardFocus().getName(),
                              this.getKeyboardFocus().getX(),
                              this.getKeyboardFocus().getY()),
                0,
                this.getHeight() - 40);
        font.draw(batch, String.format("Mouse position: [%d, %d]", Gdx.input.getX(), 720 - Gdx.input.getY()), 0, this.getHeight() - 60);
        batch.end();
    }
}
