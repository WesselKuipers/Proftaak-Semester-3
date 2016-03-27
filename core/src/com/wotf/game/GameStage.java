/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wotf.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wotf.game.classes.Game;
import com.wotf.game.classes.Team;
import com.wotf.game.classes.Unit;

/**
 *
 * @author Wessel
 */
public class GameStage extends Stage {

    private Game game;

    private SpriteBatch batch;
    private BitmapFont font;
    
    private ShapeRenderer shapeRenderer;

    public GameStage(Game game) {
        super(new ScreenViewport());
        this.game = game;

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setColor(Color.BLACK);
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
                unit.spawn(new Vector2(MathUtils.random(0, 2560 - 256), 80));

                this.addActor(unit);
            }
        }
    }

    @Override
    public void act() {
        super.act();
    }
    
    @Override
    public boolean keyDown(int keyCode) {
        
        // Temporary camera controls
        switch(keyCode) {
            case Keys.NUMPAD_2:
                ((OrthographicCamera) getCamera()).translate(new Vector2(0, -50f));
                break;
            case Keys.NUMPAD_4:
                ((OrthographicCamera) getCamera()).translate(new Vector2(-50f, 0));
                break;
            case Keys.NUMPAD_8:
                ((OrthographicCamera) getCamera()).translate(new Vector2(0, 50f));
                break;
            case Keys.NUMPAD_6:
                ((OrthographicCamera) getCamera()).translate(new Vector2(50f, 0));
                break;
            case Keys.PLUS:
                ((OrthographicCamera) getCamera()).zoom -= 0.05f;
                break;
            case Keys.MINUS:
                ((OrthographicCamera) getCamera()).zoom += 0.05f;
                break;
            case Keys.ENTER:
                ((OrthographicCamera) getCamera()).zoom = 1;
                break;    
        }
        
        getCamera().update();
        return super.keyDown(keyCode);
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
        font.draw(batch, String.format("Mouse position: screen [%d, %d], viewport %s", Gdx.input.getX(), 720 - Gdx.input.getY(), getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0))), 0, this.getHeight() - 60);
        font.draw(batch, String.format("Camera coords [%s], zoom %f", getCamera().position.toString(), ((OrthographicCamera)getCamera()).zoom), 0, this.getHeight() - 80);
        batch.end();
        
        shapeRenderer.setProjectionMatrix(getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        /*boolean terrain[][] = game.getMap().getTerrain();
        for(int x = 0; x < terrain[0].length; x++) {
            for(int y = 0; y < terrain[1].length; y++) {
                if(terrain[x][y]) {
                    shapeRenderer.point(x, y, 0);
                }
            }
        }*/
        // TODO: Figure out how to get this to work with terrain[][]
        shapeRenderer.rect(100, 0, 2360, 80);
        shapeRenderer.end();
    }
}
